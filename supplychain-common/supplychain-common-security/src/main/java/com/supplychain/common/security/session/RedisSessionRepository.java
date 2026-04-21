package com.supplychain.common.security.session;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.supplychain.common.core.constant.SupplyChainConstants;
import com.supplychain.common.core.domain.SessionUser;
import com.supplychain.common.core.enums.UserType;
import com.supplychain.common.core.exception.BizException;
import com.supplychain.common.security.config.SupplyChainJwtProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RedisSessionRepository {

    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;
    private final SupplyChainJwtProperties properties;

    public void saveSession(SessionUser sessionUser, Duration ttl) {
        if (properties.isSingleLogin()) {
            findBoundSessionId(sessionUser.getUserType(), sessionUser.getUserId())
                    .ifPresent(this::removeSession);
        }
        String payload = writeValue(sessionUser);
        stringRedisTemplate.opsForValue().set(sessionKey(sessionUser.getSessionId()), payload, ttl);
        stringRedisTemplate.opsForValue().set(userBindKey(sessionUser.getUserType(), sessionUser.getUserId()),
                sessionUser.getSessionId(), ttl);
    }

    public Optional<SessionUser> findBySessionId(String sessionId) {
        String payload = stringRedisTemplate.opsForValue().get(sessionKey(sessionId));
        if (payload == null) {
            return Optional.empty();
        }
        return Optional.of(readValue(payload));
    }

    public Optional<String> findBoundSessionId(UserType userType, Long userId) {
        return Optional.ofNullable(stringRedisTemplate.opsForValue().get(userBindKey(userType, userId)));
    }

    public boolean isCurrentSession(SessionUser sessionUser) {
        return findBoundSessionId(sessionUser.getUserType(), sessionUser.getUserId())
                .map(boundSessionId -> boundSessionId.equals(sessionUser.getSessionId()))
                .orElse(false);
    }

    public void removeSession(String sessionId) {
        findBySessionId(sessionId).ifPresent(existing -> {
            stringRedisTemplate.delete(sessionKey(sessionId));
            stringRedisTemplate.delete(userBindKey(existing.getUserType(), existing.getUserId()));
        });
    }

    private String sessionKey(String sessionId) {
        return SupplyChainConstants.REDIS_SESSION_PREFIX + sessionId;
    }

    private String userBindKey(UserType userType, Long userId) {
        return SupplyChainConstants.REDIS_USER_BIND_PREFIX + userType.name().toLowerCase() + ":" + userId;
    }

    private String writeValue(SessionUser sessionUser) {
        try {
            return objectMapper.writeValueAsString(sessionUser);
        } catch (JsonProcessingException exception) {
            throw new BizException("Redis 会话序列化失败");
        }
    }

    private SessionUser readValue(String payload) {
        try {
            return objectMapper.readValue(payload, SessionUser.class);
        } catch (JsonProcessingException exception) {
            throw new BizException("Redis 会话反序列化失败");
        }
    }
}
