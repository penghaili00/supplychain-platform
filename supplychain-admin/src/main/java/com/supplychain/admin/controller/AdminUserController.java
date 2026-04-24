package com.supplychain.admin.controller;

import com.supplychain.common.core.annotation.OperationLog;
import com.supplychain.common.core.annotation.RequirePermission;
import com.supplychain.common.core.domain.PageResult;
import com.supplychain.common.core.domain.R;
import com.supplychain.common.core.domain.SessionUser;
import com.supplychain.common.core.enums.OperationType;
import com.supplychain.common.core.exception.UnauthorizedException;
import com.supplychain.common.security.context.UserContextHolder;
import com.supplychain.service.api.admin.user.command.AdminUserCreateCommand;
import com.supplychain.service.api.admin.user.command.AdminUserStatusCommand;
import com.supplychain.service.api.admin.user.command.AdminUserUpdateCommand;
import com.supplychain.service.api.admin.user.dubbo.AdminUserDubboService;
import com.supplychain.service.api.admin.user.query.AdminUserQuery;
import com.supplychain.service.api.admin.user.view.AdminUserView;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AdminUserController {

    @DubboReference(check = false)
    private AdminUserDubboService adminUserDubboService;

    @GetMapping("/admin/users")
    @RequirePermission("sys:user:list")
    @OperationLog(title = "查询后台用户", businessType = OperationType.QUERY)
    public R<PageResult<AdminUserView>> list(AdminUserQuery query) {
        return R.ok(adminUserDubboService.listUsers(currentUser(), query));
    }

    @GetMapping("/admin/users/{userId}")
    @RequirePermission("sys:user:list")
    @OperationLog(title = "查询后台用户详情", businessType = OperationType.QUERY)
    public R<AdminUserView> detail(@PathVariable Long userId) {
        return R.ok(adminUserDubboService.getUser(currentUser(), userId));
    }

    @PostMapping("/admin/users")
    @RequirePermission("sys:user:create")
    @OperationLog(title = "创建后台用户", businessType = OperationType.CREATE)
    public R<Long> create(@Valid @RequestBody AdminUserCreateCommand command) {
        return R.ok(adminUserDubboService.createUser(command));
    }

    @PutMapping("/admin/users/{userId}")
    @RequirePermission("sys:user:update")
    @OperationLog(title = "更新后台用户", businessType = OperationType.UPDATE)
    public R<Void> update(@PathVariable Long userId, @Valid @RequestBody AdminUserUpdateCommand command) {
        adminUserDubboService.updateUser(userId, command);
        return R.ok(null);
    }

    @PutMapping("/admin/users/{userId}/status")
    @RequirePermission("sys:user:update")
    @OperationLog(title = "更新后台用户状态", businessType = OperationType.UPDATE)
    public R<Void> updateStatus(@PathVariable Long userId, @Valid @RequestBody AdminUserStatusCommand command) {
        adminUserDubboService.updateUserStatus(userId, command.getStatus());
        return R.ok(null);
    }

    private SessionUser currentUser() {
        SessionUser sessionUser = UserContextHolder.getUser();
        if (sessionUser == null) {
            throw new UnauthorizedException("未登录");
        }
        return sessionUser;
    }
}
