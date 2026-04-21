package com.supplychain.service.provider.auth.support;

import com.supplychain.common.core.exception.UnauthorizedException;
import com.supplychain.service.api.dto.AppLoginCommand;
import com.supplychain.service.provider.auth.config.AppAuthSecurityProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.time.Duration;
import java.time.Instant;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AppLoginSecurityServiceTest {

    private final StringRedisTemplate stringRedisTemplate = mock(StringRedisTemplate.class);
    private final ValueOperations<String, String> valueOperations = mock(ValueOperations.class);
    private AppAuthSecurityProperties properties;
    private AppLoginSecurityService service;

    @BeforeEach
    void setUp() {
        properties = new AppAuthSecurityProperties();
        properties.setSignSecret("SupplyChain-App-Unit-Test-Secret-2026");
        properties.setAllowedTimestampSkew(Duration.ofMinutes(5));
        properties.setNonceTtl(Duration.ofMinutes(5));

        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        service = new AppLoginSecurityService(stringRedisTemplate, properties);
    }

    @Test
    void shouldAcceptValidSignedRequest() {
        AppLoginCommand command = buildCommand(Instant.now().toEpochMilli(), "nonce-001");
        when(valueOperations.setIfAbsent(anyString(), anyString(), any(Duration.class))).thenReturn(true);

        service.validateSignedRequest(command);

        verify(valueOperations).setIfAbsent(
                "supplychain:app:auth:nonce:demo:nonce-001",
                String.valueOf(command.getTimestamp()),
                properties.getNonceTtl()
        );
    }

    @Test
    void shouldRejectExpiredSignedRequest() {
        AppLoginCommand command = buildCommand(Instant.now().minus(Duration.ofMinutes(10)).toEpochMilli(), "nonce-002");

        assertThatThrownBy(() -> service.validateSignedRequest(command))
                .isInstanceOf(UnauthorizedException.class);
    }

    @Test
    void shouldRejectDuplicateNonce() {
        AppLoginCommand command = buildCommand(Instant.now().toEpochMilli(), "nonce-003");
        when(valueOperations.setIfAbsent(anyString(), anyString(), any(Duration.class))).thenReturn(false);

        assertThatThrownBy(() -> service.validateSignedRequest(command))
                .isInstanceOf(UnauthorizedException.class);
    }

    private AppLoginCommand buildCommand(long timestamp, String nonce) {
        AppLoginCommand command = new AppLoginCommand();
        command.setUsername("demo");
        command.setPassword("Demo@123");
        command.setTimestamp(timestamp);
        command.setNonce(nonce);
        command.setSignature(signatureFor(command));
        return command;
    }

    private String signatureFor(AppLoginCommand command) {
        String payload = String.join("\n",
                command.getUsername(),
                command.getPassword(),
                String.valueOf(command.getTimestamp()),
                command.getNonce());
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(properties.getSignSecret().getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] hash = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            StringBuilder builder = new StringBuilder(hash.length * 2);
            for (byte current : hash) {
                builder.append(String.format(Locale.ROOT, "%02x", current));
            }
            return builder.toString();
        } catch (GeneralSecurityException exception) {
            throw new IllegalStateException("Unable to create test signature", exception);
        }
    }
}
