package com.supplychain.service.provider.admin.user.dubbo;

import com.supplychain.common.core.domain.SessionUser;
import com.supplychain.service.api.admin.user.dubbo.AdminUserDubboService;
import com.supplychain.service.api.admin.user.query.AdminUserQuery;
import com.supplychain.service.api.admin.user.view.AdminUserView;
import com.supplychain.service.provider.admin.user.service.AdminUserService;
import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboService;

import java.util.List;

@DubboService
@RequiredArgsConstructor
public class AdminUserDubboServiceImpl implements AdminUserDubboService {

    private final AdminUserService adminUserService;

    @Override
    public List<AdminUserView> listUsers(SessionUser requester, AdminUserQuery query) {
        return adminUserService.listUsers(requester, query);
    }
}
