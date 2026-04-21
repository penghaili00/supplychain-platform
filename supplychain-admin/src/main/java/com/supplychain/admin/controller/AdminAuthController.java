package com.supplychain.admin.controller;

import com.supplychain.common.core.annotation.OperationLog;
import com.supplychain.common.core.domain.R;
import com.supplychain.common.core.domain.SessionUser;
import com.supplychain.common.core.enums.OperationType;
import com.supplychain.common.core.exception.UnauthorizedException;
import com.supplychain.common.security.context.UserContextHolder;
import com.supplychain.service.api.dubbo.AuthDubboService;
import com.supplychain.service.api.dto.AdminLoginCommand;
import com.supplychain.service.api.dto.RefreshTokenCommand;
import com.supplychain.service.api.dto.TokenView;
import com.supplychain.service.api.dto.UserProfileView;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AdminAuthController {

    @DubboReference(check = false)
    private AuthDubboService authDubboService;

    @PostMapping("/admin/auth/login")
    @OperationLog(title = "后台登录", businessType = OperationType.LOGIN, saveRequestData = false)
    public R<TokenView> login(@Valid @RequestBody AdminLoginCommand command) {
        return R.ok(authDubboService.adminLogin(command));
    }

    @PostMapping("/admin/auth/refresh")
    @OperationLog(title = "后台刷新令牌", businessType = OperationType.LOGIN, saveRequestData = false)
    public R<TokenView> refresh(@Valid @RequestBody RefreshTokenCommand command) {
        return R.ok(authDubboService.adminRefresh(command));
    }

    @GetMapping("/admin/auth/profile")
    public R<UserProfileView> profile() {
        SessionUser sessionUser = UserContextHolder.getUser();
        if (sessionUser == null) {
            throw new UnauthorizedException("未登录");
        }
        return R.ok(authDubboService.adminProfile(sessionUser.getUserId()));
    }
}
