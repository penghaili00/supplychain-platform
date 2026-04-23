package com.supplychain.service.api.admin.user.dubbo;

import com.supplychain.common.core.domain.SessionUser;
import com.supplychain.service.api.admin.user.command.AdminUserCreateCommand;
import com.supplychain.service.api.admin.user.command.AdminUserUpdateCommand;
import com.supplychain.service.api.admin.user.query.AdminUserQuery;
import com.supplychain.service.api.admin.user.view.AdminUserView;

import java.util.List;

public interface AdminUserDubboService {

    List<AdminUserView> listUsers(SessionUser requester, AdminUserQuery query);

    AdminUserView getUser(SessionUser requester, Long userId);

    Long createUser(AdminUserCreateCommand command);

    void updateUser(Long userId, AdminUserUpdateCommand command);

    void updateUserStatus(Long userId, Integer status);
}
