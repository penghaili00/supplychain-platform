package com.supplychain.gateway.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.supplychain.common.core.constant.SupplyChainConstants;
import com.supplychain.common.core.domain.R;
import com.supplychain.common.core.domain.SessionUser;
import com.supplychain.common.core.enums.UserType;
import com.supplychain.common.security.jwt.JwtTokenProvider;
import com.supplychain.common.security.session.RedisSessionRepository;
import com.supplychain.gateway.config.SupplyChainGatewayProperties;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class JwtAuthGlobalFilter implements GlobalFilter, Ordered {

    private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    private final JwtTokenProvider jwtTokenProvider;
    private final RedisSessionRepository redisSessionRepository;
    private final SupplyChainGatewayProperties properties;
    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getPath().value();
        if (shouldIgnore(path)) {
            return chain.filter(exchange);
        }
        String authorization = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (!StringUtils.hasText(authorization) || !authorization.startsWith("Bearer ")) {
            return unauthorized(exchange, "缺少有效的 Bearer Token");
        }
        String token = authorization.substring(7);
        Claims claims;
        try {
            claims = jwtTokenProvider.parseClaims(token);
        } catch (Exception exception) {
            return unauthorized(exchange, exception.getMessage());
        }
        if (!SupplyChainConstants.TOKEN_TYPE_ACCESS.equals(claims.get(SupplyChainConstants.CLAIM_TOKEN_TYPE, String.class))) {
            return unauthorized(exchange, "accessToken 非法");
        }
        String sessionId = claims.get(SupplyChainConstants.CLAIM_SESSION_ID, String.class);
        SessionUser sessionUser = redisSessionRepository.findBySessionId(sessionId).orElse(null);
        if (sessionUser == null || !redisSessionRepository.isCurrentSession(sessionUser)) {
            return unauthorized(exchange, "登录会话已失效");
        }
        if (path.startsWith("/admin/") && sessionUser.getUserType() != UserType.ADMIN) {
            return unauthorized(exchange, "当前令牌不允许访问后台资源");
        }
        ServerHttpRequest request = exchange.getRequest().mutate()
                .header(SupplyChainConstants.HEADER_USER_ID, String.valueOf(sessionUser.getUserId()))
                .header(SupplyChainConstants.HEADER_USERNAME, sessionUser.getUsername())
                .header(SupplyChainConstants.HEADER_DISPLAY_NAME, sessionUser.getDisplayName())
                .header(SupplyChainConstants.HEADER_USER_TYPE, sessionUser.getUserType().name())
                .header(SupplyChainConstants.HEADER_DEPT_ID, sessionUser.getDeptId() == null ? "" : String.valueOf(sessionUser.getDeptId()))
                .header(SupplyChainConstants.HEADER_SESSION_ID, sessionUser.getSessionId())
                .header(SupplyChainConstants.HEADER_DATA_SCOPE, sessionUser.getDataScopeType() == null ? "" : sessionUser.getDataScopeType().name())
                .header(SupplyChainConstants.HEADER_ROLES, String.join(",", sessionUser.getRoles()))
                .header(SupplyChainConstants.HEADER_PERMISSIONS, String.join(",", sessionUser.getPermissions()))
                .build();
        return chain.filter(exchange.mutate().request(request).build());
    }

    @Override
    public int getOrder() {
        return -100;
    }

    private boolean shouldIgnore(String path) {
        return properties.getIgnoreUrls().stream().anyMatch(pattern -> PATH_MATCHER.match(pattern, path));
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange, String message) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        byte[] body = toJsonBytes(R.fail(401, message));
        DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(body);
        return exchange.getResponse().writeWith(Mono.just(buffer));
    }

    private byte[] toJsonBytes(Object body) {
        try {
            return objectMapper.writeValueAsBytes(body);
        } catch (JsonProcessingException exception) {
            return "{\"code\":401,\"message\":\"unauthorized\"}".getBytes();
        }
    }
}
