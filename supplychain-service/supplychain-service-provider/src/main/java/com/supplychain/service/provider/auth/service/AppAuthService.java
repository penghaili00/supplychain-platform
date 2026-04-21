package com.supplychain.service.provider.auth.service;

import com.supplychain.common.core.domain.SessionUser;
import com.supplychain.common.core.exception.BizException;
import com.supplychain.common.core.exception.UnauthorizedException;
import com.supplychain.service.api.dto.AppLoginCommand;
import com.supplychain.service.api.dto.TokenView;
import com.supplychain.service.api.dto.UserProfileView;
import com.supplychain.service.provider.app.user.entity.AppUser;
import com.supplychain.service.provider.app.user.mapper.AppUserMapper;
import com.supplychain.service.provider.auth.support.AppLoginSecurityService;
import com.supplychain.service.provider.auth.support.AppPasswordHasher;
import com.supplychain.service.provider.auth.support.AuthSessionUserFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppAuthService {

    private final AppUserMapper appUserMapper;
    private final AppPasswordHasher appPasswordHasher;
    private final AppLoginSecurityService appLoginSecurityService;
    private final AuthSessionUserFactory authSessionUserFactory;
    private final AuthTokenService authTokenService;

    public TokenView login(AppLoginCommand command) {
        String clientIp = appLoginSecurityService.normalizeClientIp(command.getClientIp());
        appLoginSecurityService.ensureIpAllowed(clientIp);
        try {
            appLoginSecurityService.validateSignedRequest(command);
        } catch (UnauthorizedException exception) {
            log.warn("App 登录失败，签名校验未通过，username={}，ip={}，原因={}",
                    command.getUsername(), clientIp, exception.getMessage());
            appLoginSecurityService.recordIpFailure(clientIp);
            appLoginSecurityService.ensureIpAllowed(clientIp);
            throw exception;
        }

        AppUser user = appUserMapper.selectByUsername(command.getUsername());
        if (user == null) {
            log.warn("App 登录失败，账号不存在，username={}，ip={}", command.getUsername(), clientIp);
            appLoginSecurityService.recordIpFailure(clientIp);
            appLoginSecurityService.ensureIpAllowed(clientIp);
            throw new UnauthorizedException("用户名或密码错误");
        }
        if (user.getStatus() == null || user.getStatus() != 1) {
            log.warn("App 登录失败，账号已禁用，username={}，ip={}", user.getUsername(), clientIp);
            throw new BizException(403, "账号已禁用");
        }
        try {
            appLoginSecurityService.ensureAccountAllowed(user.getUsername());
        } catch (BizException exception) {
            log.warn("App 登录失败，账号已锁定，username={}，ip={}，原因={}",
                    user.getUsername(), clientIp, exception.getMessage());
            appLoginSecurityService.recordIpFailure(clientIp);
            appLoginSecurityService.ensureIpAllowed(clientIp);
            throw exception;
        }
        if (!appPasswordHasher.matches(command.getPassword(), user.getPasswordSalt(), user.getPasswordHash())) {
            log.warn("App 登录失败，密码错误，username={}，ip={}", user.getUsername(), clientIp);
            appLoginSecurityService.recordBadCredentials(user.getUsername(), clientIp);
            appLoginSecurityService.ensureIpAllowed(clientIp);
            appLoginSecurityService.ensureAccountAllowed(user.getUsername());
            throw new UnauthorizedException("用户名或密码错误");
        }
        appLoginSecurityService.clearAccountFailures(user.getUsername());
        return authTokenService.issueToken(authSessionUserFactory.buildAppSessionUser(user));
    }

    public UserProfileView profile(Long userId) {
        AppUser user = appUserMapper.selectByUserId(userId);
        if (user == null) {
            throw new BizException(404, "用户不存在");
        }
        return toProfileView(authSessionUserFactory.buildAppSessionUser(user));
    }

    private UserProfileView toProfileView(SessionUser sessionUser) {
        return UserProfileView.builder()
                .userId(sessionUser.getUserId())
                .username(sessionUser.getUsername())
                .displayName(sessionUser.getDisplayName())
                .userType(sessionUser.getUserType())
                .deptId(sessionUser.getDeptId())
                .dataScopeType(sessionUser.getDataScopeType())
                .roles(sessionUser.getRoles())
                .permissions(sessionUser.getPermissions())
                .build();
    }
}
