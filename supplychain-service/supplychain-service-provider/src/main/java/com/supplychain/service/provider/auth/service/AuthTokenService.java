package com.supplychain.service.provider.auth.service;

import com.supplychain.common.core.constant.SupplyChainConstants;
import com.supplychain.common.core.domain.SessionUser;
import com.supplychain.common.core.enums.UserType;
import com.supplychain.common.core.exception.UnauthorizedException;
import com.supplychain.common.security.jwt.JwtTokenProvider;
import com.supplychain.common.security.session.RedisSessionRepository;
import com.supplychain.service.api.auth.command.RefreshTokenCommand;
import com.supplychain.service.api.auth.view.TokenView;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthTokenService {

    private final JwtTokenProvider jwtTokenProvider;
    private final RedisSessionRepository redisSessionRepository;

    public TokenView issueToken(SessionUser sessionUser) {
        redisSessionRepository.saveSession(sessionUser, jwtTokenProvider.getRefreshExpire());
        return buildTokenView(sessionUser);
    }

    public TokenView refresh(RefreshTokenCommand command, UserType userType) {
        Claims claims = jwtTokenProvider.parseClaims(command.getRefreshToken());
        String tokenType = claims.get(SupplyChainConstants.CLAIM_TOKEN_TYPE, String.class);
        if (!SupplyChainConstants.TOKEN_TYPE_REFRESH.equals(tokenType)) {
            throw new UnauthorizedException("refreshToken 非法");
        }
        if (!userType.name().equals(claims.get(SupplyChainConstants.CLAIM_USER_TYPE, String.class))) {
            throw new UnauthorizedException("Token 用户类型不匹配");
        }
        String sessionId = claims.get(SupplyChainConstants.CLAIM_SESSION_ID, String.class);
        SessionUser sessionUser = redisSessionRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new UnauthorizedException("登录会话已失效"));
        if (!redisSessionRepository.isCurrentSession(sessionUser)) {
            throw new UnauthorizedException("账号已在其他位置登录");
        }
        redisSessionRepository.saveSession(sessionUser, jwtTokenProvider.getRefreshExpire());
        return buildTokenView(sessionUser);
    }

    private TokenView buildTokenView(SessionUser sessionUser) {
        LocalDateTime now = LocalDateTime.now();
        return TokenView.builder()
                .accessToken(jwtTokenProvider.createAccessToken(sessionUser))
                .refreshToken(jwtTokenProvider.createRefreshToken(sessionUser))
                .accessExpireAt(now.plus(jwtTokenProvider.getAccessExpire()))
                .refreshExpireAt(now.plus(jwtTokenProvider.getRefreshExpire()))
                .user(sessionUser)
                .build();
    }
}
