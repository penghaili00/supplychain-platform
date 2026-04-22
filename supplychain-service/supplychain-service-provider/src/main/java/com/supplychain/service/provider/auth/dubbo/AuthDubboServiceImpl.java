package com.supplychain.service.provider.auth.dubbo;

import com.supplychain.common.core.enums.UserType;
import com.supplychain.service.api.auth.dubbo.AuthDubboService;
import com.supplychain.service.api.admin.user.command.AdminLoginCommand;
import com.supplychain.service.api.app.user.command.AppLoginCommand;
import com.supplychain.service.api.auth.command.RefreshTokenCommand;
import com.supplychain.service.api.auth.view.TokenView;
import com.supplychain.service.api.app.user.view.UserProfileView;
import com.supplychain.service.provider.auth.service.AdminAuthService;
import com.supplychain.service.provider.auth.service.AppAuthService;
import com.supplychain.service.provider.auth.service.AuthTokenService;
import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboService;

@DubboService
@RequiredArgsConstructor
public class AuthDubboServiceImpl implements AuthDubboService {

    private final AdminAuthService adminAuthService;
    private final AppAuthService appAuthService;
    private final AuthTokenService authTokenService;

    @Override
    public TokenView adminLogin(AdminLoginCommand command) {
        return adminAuthService.login(command);
    }

    @Override
    public TokenView appLogin(AppLoginCommand command) {
        return appAuthService.login(command);
    }

    @Override
    public TokenView adminRefresh(RefreshTokenCommand command) {
        return authTokenService.refresh(command, UserType.ADMIN);
    }

    @Override
    public TokenView appRefresh(RefreshTokenCommand command) {
        return authTokenService.refresh(command, UserType.APP);
    }

    @Override
    public UserProfileView adminProfile(Long userId) {
        return adminAuthService.profile(userId);
    }

    @Override
    public UserProfileView appProfile(Long userId) {
        return appAuthService.profile(userId);
    }
}
