package com.supplychain.service.provider.auth.service;

import com.supplychain.common.core.domain.SessionUser;
import com.supplychain.common.core.exception.BizException;
import com.supplychain.common.core.exception.UnauthorizedException;
import com.supplychain.service.api.dto.AdminLoginCommand;
import com.supplychain.service.api.dto.TokenView;
import com.supplychain.service.api.dto.UserProfileView;
import com.supplychain.service.provider.admin.user.entity.SysUser;
import com.supplychain.service.provider.admin.user.mapper.SysUserMapper;
import com.supplychain.service.provider.auth.support.AuthSessionUserFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminAuthService {

    private final SysUserMapper sysUserMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthSessionUserFactory authSessionUserFactory;
    private final AuthTokenService authTokenService;

    public TokenView login(AdminLoginCommand command) {
        SysUser user = sysUserMapper.selectByUsername(command.getUsername());
        if (user == null || user.getStatus() == null || user.getStatus() != 1) {
            log.warn("后台登录失败，账号不存在或已禁用，username={}", command.getUsername());
            throw new UnauthorizedException("用户名或密码错误");
        }
        if (!passwordEncoder.matches(command.getPassword(), user.getPassword())) {
            log.warn("后台登录失败，密码错误，username={}", command.getUsername());
            throw new UnauthorizedException("用户名或密码错误");
        }
        return authTokenService.issueToken(authSessionUserFactory.buildAdminSessionUser(user));
    }

    public UserProfileView profile(Long userId) {
        SysUser user = sysUserMapper.selectByUserId(userId);
        if (user == null) {
            throw new BizException(404, "用户不存在");
        }
        return toProfileView(authSessionUserFactory.buildAdminSessionUser(user));
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
