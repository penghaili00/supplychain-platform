package com.supplychain.service.api.auth.dubbo;

import com.supplychain.service.api.admin.user.command.AdminLoginCommand;
import com.supplychain.service.api.app.user.command.AppLoginCommand;
import com.supplychain.service.api.auth.command.RefreshTokenCommand;
import com.supplychain.service.api.auth.view.TokenView;
import com.supplychain.service.api.app.user.view.UserProfileView;

public interface AuthDubboService {

    TokenView adminLogin(AdminLoginCommand command);

    TokenView appLogin(AppLoginCommand command);

    TokenView adminRefresh(RefreshTokenCommand command);

    TokenView appRefresh(RefreshTokenCommand command);

    UserProfileView adminProfile(Long userId);

    UserProfileView appProfile(Long userId);
}
