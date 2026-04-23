package com.supplychain.admin.controller;

import com.supplychain.common.core.annotation.OperationLog;
import com.supplychain.common.core.annotation.RequirePermission;
import com.supplychain.common.core.domain.R;
import com.supplychain.common.core.enums.OperationType;
import com.supplychain.service.api.rbac.role.command.RoleCreateCommand;
import com.supplychain.service.api.rbac.role.command.RoleStatusCommand;
import com.supplychain.service.api.rbac.role.command.RoleUpdateCommand;
import com.supplychain.service.api.rbac.role.dubbo.AdminRoleDubboService;
import com.supplychain.service.api.rbac.role.query.RoleQuery;
import com.supplychain.service.api.rbac.role.view.RoleView;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class AdminRoleController {

    @DubboReference(check = false)
    private AdminRoleDubboService adminRoleDubboService;

    @GetMapping("/admin/roles")
    @RequirePermission("sys:role:list")
    @OperationLog(title = "查询角色列表", businessType = OperationType.QUERY)
    public R<List<RoleView>> list(RoleQuery query) {
        return R.ok(adminRoleDubboService.listRoles(query));
    }

    @GetMapping("/admin/roles/{roleId}")
    @RequirePermission("sys:role:list")
    @OperationLog(title = "查询角色详情", businessType = OperationType.QUERY)
    public R<RoleView> detail(@PathVariable Long roleId) {
        return R.ok(adminRoleDubboService.getRole(roleId));
    }

    @PostMapping("/admin/roles")
    @RequirePermission("sys:role:create")
    @OperationLog(title = "创建角色", businessType = OperationType.CREATE)
    public R<Long> create(@Valid @RequestBody RoleCreateCommand command) {
        return R.ok(adminRoleDubboService.createRole(command));
    }

    @PutMapping("/admin/roles/{roleId}")
    @RequirePermission("sys:role:update")
    @OperationLog(title = "更新角色", businessType = OperationType.UPDATE)
    public R<Void> update(@PathVariable Long roleId, @Valid @RequestBody RoleUpdateCommand command) {
        adminRoleDubboService.updateRole(roleId, command);
        return R.ok(null);
    }

    @PutMapping("/admin/roles/{roleId}/status")
    @RequirePermission("sys:role:update")
    @OperationLog(title = "更新角色状态", businessType = OperationType.UPDATE)
    public R<Void> updateStatus(@PathVariable Long roleId, @Valid @RequestBody RoleStatusCommand command) {
        adminRoleDubboService.updateRoleStatus(roleId, command.getStatus());
        return R.ok(null);
    }
}
