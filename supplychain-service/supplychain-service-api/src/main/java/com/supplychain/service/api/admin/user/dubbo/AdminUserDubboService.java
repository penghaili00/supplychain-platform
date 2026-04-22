package com.supplychain.service.api.admin.user.dubbo;

import com.supplychain.common.core.domain.SessionUser;
import com.supplychain.service.api.admin.user.query.AdminUserQuery;
import com.supplychain.service.api.admin.user.view.AdminUserView;

import java.util.List;

public interface AdminUserDubboService {

    List<AdminUserView> listUsers(SessionUser requester, AdminUserQuery query);
}
