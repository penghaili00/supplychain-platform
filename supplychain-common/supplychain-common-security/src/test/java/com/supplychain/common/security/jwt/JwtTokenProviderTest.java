package com.supplychain.common.security.jwt;

import com.supplychain.common.core.constant.SupplyChainConstants;
import com.supplychain.common.core.domain.SessionUser;
import com.supplychain.common.core.enums.UserType;
import com.supplychain.common.core.exception.UnauthorizedException;
import com.supplychain.common.security.config.SupplyChainJwtProperties;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtTokenProviderTest {

    @Test
    void shouldCreateAndParseAccessToken() {
        JwtTokenProvider provider = new JwtTokenProvider(jwtProperties());
        provider.init();

        SessionUser sessionUser = SessionUser.builder()
                .userId(1001L)
                .username("admin")
                .displayName("SupplyChain Admin")
                .userType(UserType.ADMIN)
                .deptId(100L)
                .sessionId("session-1001")
                .build();

        String token = provider.createAccessToken(sessionUser);
        Claims claims = provider.parseClaims(token);

        assertThat(claims.getSubject()).isEqualTo("1001");
        assertThat(claims.getIssuer()).isEqualTo("supplychain-test");
        assertThat(claims.get(SupplyChainConstants.CLAIM_USERNAME, String.class)).isEqualTo("admin");
        assertThat(claims.get(SupplyChainConstants.CLAIM_USER_TYPE, String.class)).isEqualTo(UserType.ADMIN.name());
        assertThat(claims.get(SupplyChainConstants.CLAIM_TOKEN_TYPE, String.class)).isEqualTo(SupplyChainConstants.TOKEN_TYPE_ACCESS);
    }

    @Test
    void shouldRejectInvalidToken() {
        JwtTokenProvider provider = new JwtTokenProvider(jwtProperties());
        provider.init();

        assertThatThrownBy(() -> provider.parseClaims("invalid-token"))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("Token");
    }

    private SupplyChainJwtProperties jwtProperties() {
        SupplyChainJwtProperties properties = new SupplyChainJwtProperties();
        properties.setIssuer("supplychain-test");
        properties.setSecret("SupplyChain-Unit-Test-Secret-Key-For-Jwt-2026-0123456789");
        properties.setAccessExpire(Duration.ofMinutes(15));
        properties.setRefreshExpire(Duration.ofDays(3));
        return properties;
    }
}
