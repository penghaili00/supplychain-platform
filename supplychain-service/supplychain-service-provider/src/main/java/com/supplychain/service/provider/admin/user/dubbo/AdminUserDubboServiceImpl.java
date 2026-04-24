package com.supplychain.service.provider.admin.user.dubbo;

import com.supplychain.common.core.domain.PageResult;
import com.supplychain.common.core.domain.SessionUser;
import com.supplychain.service.api.admin.user.command.AdminUserCreateCommand;
import com.supplychain.service.api.admin.user.command.AdminUserUpdateCommand;
import com.supplychain.service.api.admin.user.dubbo.AdminUserDubboService;
import com.supplychain.service.api.admin.user.query.AdminUserQuery;
import com.supplychain.service.api.admin.user.view.AdminUserView;
import com.supplychain.service.provider.admin.user.service.AdminUserService;
import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboService;

@DubboService
@RequiredArgsConstructor
public class AdminUserDubboServiceImpl implements AdminUserDubboService {

    private final AdminUserService adminUserService;

    @Override
    public PageResult<AdminUserView> listUsers(SessionUser requester, AdminUserQuery query) {
        return adminUserService.listUsers(requester, query);
    }

    @Override
    public AdminUserView getUser(SessionUser requester, Long userId) {
        return adminUserService.getUser(requester, userId);
    }

    @Override
    public Long createUser(AdminUserCreateCommand command) {
        return adminUserService.createUser(command);
    }

    @Override
    public void updateUser(Long userId, AdminUserUpdateCommand command) {
        adminUserService.updateUser(userId, command);
    }

    @Override
    public void updateUserStatus(Long userId, Integer status) {
        adminUserService.updateUserStatus(userId, status);
    }
}
