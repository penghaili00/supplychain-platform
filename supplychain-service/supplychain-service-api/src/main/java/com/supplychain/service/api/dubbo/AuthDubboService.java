package com.supplychain.service.api.dubbo;

import com.supplychain.service.api.dto.AdminLoginCommand;
import com.supplychain.service.api.dto.AppLoginCommand;
import com.supplychain.service.api.dto.RefreshTokenCommand;
import com.supplychain.service.api.dto.TokenView;
import com.supplychain.service.api.dto.UserProfileView;

public interface AuthDubboService {

    TokenView adminLogin(AdminLoginCommand command);

    TokenView appLogin(AppLoginCommand command);

    TokenView adminRefresh(RefreshTokenCommand command);

    TokenView appRefresh(RefreshTokenCommand command);

    UserProfileView adminProfile(Long userId);

    UserProfileView appProfile(Long userId);
}
