package com.supplychain.common.security.aspect;

import com.supplychain.common.core.annotation.RequirePermission;
import com.supplychain.common.core.domain.SessionUser;
import com.supplychain.common.core.exception.ForbiddenException;
import com.supplychain.common.core.exception.UnauthorizedException;
import com.supplychain.common.security.context.UserContextHolder;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

@Aspect
@Component
public class PermissionAspect {

    private static final String SUPER_ADMIN_ROLE_KEY = "super_admin";

    @Before("@annotation(requirePermission)")
    public void checkPermission(JoinPoint joinPoint, RequirePermission requirePermission) {
        SessionUser sessionUser = UserContextHolder.getUser();
        if (sessionUser == null) {
            throw new UnauthorizedException("未登录或登录状态已失效");
        }
        if (!CollectionUtils.isEmpty(sessionUser.getRoles())
                && sessionUser.getRoles().contains(SUPER_ADMIN_ROLE_KEY)) {
            return;
        }
        if (CollectionUtils.isEmpty(sessionUser.getPermissions())
                || (!sessionUser.getPermissions().contains(requirePermission.value())
                && !sessionUser.getPermissions().contains("*:*:*"))) {
            throw new ForbiddenException("权限不足: " + requirePermission.value());
        }
    }
}
