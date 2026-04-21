package com.supplychain.service.provider.auth.support;

import com.supplychain.common.core.domain.SessionUser;
import com.supplychain.common.core.enums.DataScopeType;
import com.supplychain.common.core.enums.UserType;
import com.supplychain.service.provider.admin.user.entity.SysUser;
import com.supplychain.service.provider.app.user.entity.AppUser;
import com.supplychain.service.provider.rbac.entity.SysRole;
import com.supplychain.service.provider.rbac.mapper.SysMenuMapper;
import com.supplychain.service.provider.rbac.mapper.SysRoleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AuthSessionUserFactory {

    private final SysRoleMapper sysRoleMapper;
    private final SysMenuMapper sysMenuMapper;

    public SessionUser buildAdminSessionUser(SysUser user) {
        List<SysRole> roles = sysRoleMapper.listByAdminUserId(user.getId());
        List<String> permissions = sysMenuMapper.listPermissionCodesByAdminUserId(user.getId());
        return buildSessionUser(user.getId(), user.getUsername(), user.getDisplayName(), UserType.ADMIN,
                user.getDeptId(), roles, permissions);
    }

    public SessionUser buildAppSessionUser(AppUser user) {
        List<SysRole> roles = sysRoleMapper.listByAppUserId(user.getId());
        List<String> permissions = sysMenuMapper.listPermissionCodesByAppUserId(user.getId());
        return buildSessionUser(user.getId(), user.getUsername(), user.getDisplayName(), UserType.APP,
                null, roles, permissions);
    }

    private SessionUser buildSessionUser(Long userId, String username, String displayName, UserType userType,
                                         Long deptId, List<SysRole> roles, List<String> permissions) {
        List<String> roleKeys = roles.stream().map(SysRole::getRoleKey).toList();
        return SessionUser.builder()
                .userId(userId)
                .username(username)
                .displayName(displayName)
                .userType(userType)
                .deptId(deptId)
                .sessionId(UUID.randomUUID().toString().replace("-", ""))
                .dataScopeType(resolveDataScope(roles))
                .roles(roleKeys)
                .permissions(CollectionUtils.isEmpty(permissions) ? List.of() : permissions)
                .build();
    }

    private DataScopeType resolveDataScope(List<SysRole> roles) {
        if (CollectionUtils.isEmpty(roles)) {
            return DataScopeType.SELF;
        }
        return roles.stream()
                .map(SysRole::getDataScope)
                .map(DataScopeType::fromCode)
                .min(Comparator.comparingInt(this::priority))
                .orElse(DataScopeType.SELF);
    }

    private int priority(DataScopeType dataScopeType) {
        return switch (dataScopeType) {
            case ALL -> 1;
            case DEPT_AND_CHILD -> 2;
            case DEPT -> 3;
            case SELF -> 4;
        };
    }
}
