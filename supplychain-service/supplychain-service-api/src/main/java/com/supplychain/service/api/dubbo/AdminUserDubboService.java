package com.supplychain.service.api.dubbo;

import com.supplychain.common.core.domain.SessionUser;
import com.supplychain.service.api.dto.AdminUserQuery;
import com.supplychain.service.api.dto.AdminUserView;

import java.util.List;

public interface AdminUserDubboService {

    List<AdminUserView> listUsers(SessionUser requester, AdminUserQuery query);
}
