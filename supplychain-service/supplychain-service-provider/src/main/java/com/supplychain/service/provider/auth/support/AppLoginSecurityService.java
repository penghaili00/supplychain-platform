package com.supplychain.service.provider.auth.support;

import com.supplychain.common.core.exception.BizException;
import com.supplychain.common.core.exception.UnauthorizedException;
import com.supplychain.service.api.dto.AppLoginCommand;
import com.supplychain.service.provider.auth.config.AppAuthSecurityProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Slf4j
@Component
@RequiredArgsConstructor
public class AppLoginSecurityService {

    private static final String UNKNOWN_IP = "unknown";

    private final StringRedisTemplate stringRedisTemplate;
    private final AppAuthSecurityProperties properties;

    public String normalizeClientIp(String clientIp) {
        return StringUtils.hasText(clientIp) ? clientIp.trim() : UNKNOWN_IP;
    }

    public void ensureIpAllowed(String clientIp) {
        assertNotLocked(ipLockKey(normalizeClientIp(clientIp)), "IP");
    }

    public void ensureAccountAllowed(String username) {
        assertNotLocked(accountLockKey(username), "账号");
    }

    public void validateSignedRequest(AppLoginCommand command) {
        long now = Instant.now().toEpochMilli();
        long skew = Math.abs(now - command.getTimestamp());
        if (skew > properties.getAllowedTimestampSkew().toMillis()) {
            throw new UnauthorizedException("登录签名已过期");
        }
        String expected = hmacSha256Hex(properties.getSignSecret(), buildSignaturePayload(command));
        if (!secureEquals(expected, command.getSignature())) {
            throw new UnauthorizedException("登录签名无效");
        }
        Boolean firstRequest = stringRedisTemplate.opsForValue().setIfAbsent(
                nonceKey(command.getUsername(), command.getNonce()),
                String.valueOf(command.getTimestamp()),
                properties.getNonceTtl()
        );
        if (!Boolean.TRUE.equals(firstRequest)) {
            throw new UnauthorizedException("登录请求重复，请勿重放");
        }
    }

    public void recordIpFailure(String clientIp) {
        applyRules(normalizeClientIp(clientIp), properties.getIpFailureRules(), false);
    }

    public void recordBadCredentials(String username, String clientIp) {
        applyRules(username, properties.getAccountFailureRules(), true);
        applyRules(normalizeClientIp(clientIp), properties.getIpFailureRules(), false);
    }

    public void clearAccountFailures(String username) {
        if (!StringUtils.hasText(username)) {
            return;
        }
        List<String> keys = new ArrayList<>();
        for (AppAuthSecurityProperties.LimitRule rule : safeRules(properties.getAccountFailureRules())) {
            keys.add(failureCounterKey(true, username, rule));
        }
        if (!keys.isEmpty()) {
            stringRedisTemplate.delete(keys);
        }
    }

    private void applyRules(String subject, List<AppAuthSecurityProperties.LimitRule> rules, boolean accountRule) {
        if (!StringUtils.hasText(subject) || CollectionUtils.isEmpty(rules)) {
            return;
        }
        for (AppAuthSecurityProperties.LimitRule rule : safeRules(rules)) {
            String counterKey = failureCounterKey(accountRule, subject, rule);
            Long count = stringRedisTemplate.opsForValue().increment(counterKey);
            if (count != null && count == 1L) {
                stringRedisTemplate.expire(counterKey, rule.getWindow());
            }
            if (count != null && count >= rule.getThreshold()) {
                extendLock(accountRule ? accountLockKey(subject) : ipLockKey(subject), rule.getLockDuration(),
                        accountRule ? "账号" : "IP", subject);
            }
        }
    }

    private void extendLock(String lockKey, Duration lockDuration, String label, String subject) {
        Long remainSeconds = stringRedisTemplate.getExpire(lockKey);
        if (remainSeconds == null || remainSeconds < lockDuration.toSeconds()) {
            stringRedisTemplate.opsForValue().set(lockKey, "1", lockDuration);
            log.warn("{}触发登录失败锁定，标识={}，锁定时长={}", label, subject, formatDuration(lockDuration));
        }
    }

    private void assertNotLocked(String lockKey, String label) {
        Long remainSeconds = stringRedisTemplate.getExpire(lockKey);
        if (remainSeconds == null || remainSeconds == -2) {
            return;
        }
        if (remainSeconds == -1) {
            throw new BizException(423, label + "已锁定，请联系管理员处理");
        }
        if (remainSeconds > 0) {
            throw new BizException(423, label + "已锁定，剩余" + formatDuration(Duration.ofSeconds(remainSeconds)));
        }
    }

    private List<AppAuthSecurityProperties.LimitRule> safeRules(List<AppAuthSecurityProperties.LimitRule> rules) {
        return rules == null ? List.of() : rules;
    }

    private String buildSignaturePayload(AppLoginCommand command) {
        return String.join("\n",
                command.getUsername(),
                command.getPassword(),
                String.valueOf(command.getTimestamp()),
                command.getNonce());
    }

    private String hmacSha256Hex(String secret, String payload) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] hash = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            StringBuilder builder = new StringBuilder(hash.length * 2);
            for (byte current : hash) {
                builder.append(String.format(Locale.ROOT, "%02x", current));
            }
            return builder.toString();
        } catch (GeneralSecurityException exception) {
            throw new BizException("App 登录签名校验失败");
        }
    }

    private boolean secureEquals(String left, String right) {
        if (!StringUtils.hasText(left) || !StringUtils.hasText(right)) {
            return false;
        }
        return MessageDigest.isEqual(
                left.toLowerCase(Locale.ROOT).getBytes(StandardCharsets.UTF_8),
                right.toLowerCase(Locale.ROOT).getBytes(StandardCharsets.UTF_8)
        );
    }

    private String failureCounterKey(boolean accountRule, String subject, AppAuthSecurityProperties.LimitRule rule) {
        return (accountRule ? "supplychain:app:auth:fail:user:" : "supplychain:app:auth:fail:ip:")
                + subject + ":" + rule.getWindow().toSeconds();
    }

    private String accountLockKey(String username) {
        return "supplychain:app:auth:lock:user:" + username;
    }

    private String ipLockKey(String clientIp) {
        return "supplychain:app:auth:lock:ip:" + clientIp;
    }

    private String nonceKey(String username, String nonce) {
        return "supplychain:app:auth:nonce:" + username + ":" + nonce;
    }

    private String formatDuration(Duration duration) {
        long totalSeconds = Math.max(1L, duration.toSeconds());
        long days = totalSeconds / 86400;
        long hours = (totalSeconds % 86400) / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;
        StringBuilder builder = new StringBuilder();
        if (days > 0) {
            builder.append(days).append("天");
        }
        if (hours > 0) {
            builder.append(hours).append("小时");
        }
        if (minutes > 0) {
            builder.append(minutes).append("分钟");
        }
        if (builder.isEmpty() || seconds > 0) {
            builder.append(seconds).append("秒");
        }
        return builder.toString();
    }
}
