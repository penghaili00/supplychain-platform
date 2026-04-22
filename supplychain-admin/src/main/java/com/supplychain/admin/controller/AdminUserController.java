package com.supplychain.admin.controller;

import com.supplychain.common.core.annotation.OperationLog;
import com.supplychain.common.core.annotation.RequirePermission;
import com.supplychain.common.core.domain.R;
import com.supplychain.common.core.domain.SessionUser;
import com.supplychain.common.core.enums.OperationType;
import com.supplychain.common.core.exception.UnauthorizedException;
import com.supplychain.common.security.context.UserContextHolder;
import com.supplychain.service.api.admin.user.dubbo.AdminUserDubboService;
import com.supplychain.service.api.admin.user.query.AdminUserQuery;
import com.supplychain.service.api.admin.user.view.AdminUserView;
import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class AdminUserController {

    @DubboReference(check = false)
    private AdminUserDubboService adminUserDubboService;

    @GetMapping("/admin/users")
    @RequirePermission("sys:user:list")
    @OperationLog(title = "查询后台用户", businessType = OperationType.QUERY)
    public R<List<AdminUserView>> list(AdminUserQuery query) {
        SessionUser sessionUser = UserContextHolder.getUser();
        if (sessionUser == null) {
            throw new UnauthorizedException("未登录");
        }
        return R.ok(adminUserDubboService.listUsers(sessionUser, query));
    }
}
