package com.supplychain.common.security.jwt;

import com.supplychain.common.core.constant.SupplyChainConstants;
import com.supplychain.common.core.domain.SessionUser;
import com.supplychain.common.core.exception.UnauthorizedException;
import com.supplychain.common.security.config.SupplyChainJwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final SupplyChainJwtProperties properties;

    private SecretKey secretKey;

    @PostConstruct
    void init() {
        byte[] keyBytes = properties.getSecret().getBytes(StandardCharsets.UTF_8);
        secretKey = Keys.hmacShaKeyFor(keyBytes);
    }

    public String createAccessToken(SessionUser sessionUser) {
        return createToken(sessionUser, SupplyChainConstants.TOKEN_TYPE_ACCESS, properties.getAccessExpire());
    }

    public String createRefreshToken(SessionUser sessionUser) {
        return createToken(sessionUser, SupplyChainConstants.TOKEN_TYPE_REFRESH, properties.getRefreshExpire());
    }

    public Claims parseClaims(String token) {
        try {
            Jws<Claims> claimsJws = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token);
            return claimsJws.getPayload();
        } catch (JwtException | IllegalArgumentException exception) {
            throw new UnauthorizedException("Token 非法或已过期");
        }
    }

    public Duration getAccessExpire() {
        return properties.getAccessExpire();
    }

    public Duration getRefreshExpire() {
        return properties.getRefreshExpire();
    }

    private String createToken(SessionUser sessionUser, String tokenType, Duration duration) {
        Instant now = Instant.now();
        return Jwts.builder()
                .issuer(properties.getIssuer())
                .subject(String.valueOf(sessionUser.getUserId()))
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(duration)))
                .claim(SupplyChainConstants.CLAIM_SESSION_ID, sessionUser.getSessionId())
                .claim(SupplyChainConstants.CLAIM_USERNAME, sessionUser.getUsername())
                .claim(SupplyChainConstants.CLAIM_DISPLAY_NAME, sessionUser.getDisplayName())
                .claim(SupplyChainConstants.CLAIM_USER_TYPE, sessionUser.getUserType().name())
                .claim(SupplyChainConstants.CLAIM_DEPT_ID, sessionUser.getDeptId())
                .claim(SupplyChainConstants.CLAIM_TOKEN_TYPE, tokenType)
                .signWith(secretKey)
                .compact();
    }
}
