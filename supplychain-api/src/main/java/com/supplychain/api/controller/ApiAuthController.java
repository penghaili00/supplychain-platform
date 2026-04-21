package com.supplychain.api.controller;

import com.supplychain.common.core.annotation.OperationLog;
import com.supplychain.common.core.domain.R;
import com.supplychain.common.core.domain.SessionUser;
import com.supplychain.common.core.enums.OperationType;
import com.supplychain.common.core.exception.UnauthorizedException;
import com.supplychain.common.security.context.UserContextHolder;
import com.supplychain.service.api.dubbo.AuthDubboService;
import com.supplychain.service.api.dto.AppLoginCommand;
import com.supplychain.service.api.dto.RefreshTokenCommand;
import com.supplychain.service.api.dto.TokenView;
import com.supplychain.service.api.dto.UserProfileView;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequiredArgsConstructor
public class ApiAuthController {

    @DubboReference(check = false)
    private AuthDubboService authDubboService;

    @PostMapping("/api/auth/login")
    @OperationLog(title = "用户登录", businessType = OperationType.LOGIN, saveRequestData = false)
    public R<TokenView> login(@Valid @RequestBody AppLoginCommand command, HttpServletRequest request) {
        command.setClientIp(resolveClientIp(request));
        return R.ok(authDubboService.appLogin(command));
    }

    @PostMapping("/api/auth/refresh")
    @OperationLog(title = "用户刷新令牌", businessType = OperationType.LOGIN, saveRequestData = false)
    public R<TokenView> refresh(@Valid @RequestBody RefreshTokenCommand command) {
        return R.ok(authDubboService.appRefresh(command));
    }

    @GetMapping("/api/auth/profile")
    @OperationLog(title = "查看用户资料", businessType = OperationType.QUERY)
    public R<UserProfileView> profile() {
        SessionUser sessionUser = UserContextHolder.getUser();
        if (sessionUser == null) {
            throw new UnauthorizedException("未登录");
        }
        return R.ok(authDubboService.appProfile(sessionUser.getUserId()));
    }

    private String resolveClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (StringUtils.hasText(xForwardedFor)) {
            return xForwardedFor.split(",")[0].trim();
        }
        String xRealIp = request.getHeader("X-Real-IP");
        if (StringUtils.hasText(xRealIp)) {
            return xRealIp.trim();
        }
        return request.getRemoteAddr();
    }
}
