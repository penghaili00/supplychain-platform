package com.supplychain.service.provider.rbac.dubbo;

import com.supplychain.service.api.rbac.role.command.RoleCreateCommand;
import com.supplychain.service.api.rbac.role.command.RoleUpdateCommand;
import com.supplychain.service.api.rbac.role.dubbo.AdminRoleDubboService;
import com.supplychain.service.api.rbac.role.query.RoleQuery;
import com.supplychain.service.api.rbac.role.view.RoleView;
import com.supplychain.service.provider.rbac.service.AdminRoleService;
import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboService;

import java.util.List;

@DubboService
@RequiredArgsConstructor
public class AdminRoleDubboServiceImpl implements AdminRoleDubboService {

    private final AdminRoleService adminRoleService;

    @Override
    public List<RoleView> listRoles(RoleQuery query) {
        return adminRoleService.listRoles(query);
    }

    @Override
    public RoleView getRole(Long roleId) {
        return adminRoleService.getRole(roleId);
    }

    @Override
    public Long createRole(RoleCreateCommand command) {
        return adminRoleService.createRole(command);
    }

    @Override
    public void updateRole(Long roleId, RoleUpdateCommand command) {
        adminRoleService.updateRole(roleId, command);
    }

    @Override
    public void updateRoleStatus(Long roleId, Integer status) {
        adminRoleService.updateRoleStatus(roleId, status);
    }
}
