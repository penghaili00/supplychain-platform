package com.supplychain.service.api.rbac.role.dubbo;

import com.supplychain.service.api.rbac.role.command.RoleCreateCommand;
import com.supplychain.service.api.rbac.role.command.RoleUpdateCommand;
import com.supplychain.service.api.rbac.role.query.RoleQuery;
import com.supplychain.service.api.rbac.role.view.RoleView;

import java.util.List;

public interface AdminRoleDubboService {

    List<RoleView> listRoles(RoleQuery query);

    RoleView getRole(Long roleId);

    Long createRole(RoleCreateCommand command);

    void updateRole(Long roleId, RoleUpdateCommand command);

    void updateRoleStatus(Long roleId, Integer status);
}
