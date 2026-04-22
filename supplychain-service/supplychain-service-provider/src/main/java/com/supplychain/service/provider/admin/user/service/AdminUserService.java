package com.supplychain.service.provider.admin.user.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.supplychain.common.core.domain.SessionUser;
import com.supplychain.common.core.enums.UserType;
import com.supplychain.common.mybatis.support.DataScopeSqlSupport;
import com.supplychain.service.api.admin.user.query.AdminUserQuery;
import com.supplychain.service.api.admin.user.view.AdminUserView;
import com.supplychain.service.provider.admin.user.entity.SysUser;
import com.supplychain.service.provider.admin.user.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminUserService {

    private final SysUserMapper sysUserMapper;
    private final DataScopeSqlSupport dataScopeSqlSupport;

    public List<AdminUserView> listUsers(SessionUser requester, AdminUserQuery query) {
        LambdaQueryWrapper<SysUser> wrapper = Wrappers.lambdaQuery(SysUser.class);
        wrapper.eq(SysUser::getDeleted, 0);
        if (query != null && StringUtils.hasText(query.getKeyword())) {
            wrapper.and(q -> q.like(SysUser::getUsername, query.getKeyword())
                    .or()
                    .like(SysUser::getDisplayName, query.getKeyword()));
        }
        dataScopeSqlSupport.apply(wrapper, requester, SysUser::getDeptId, SysUser::getId, SysUser::getDeptAncestors);
        wrapper.orderByAsc(SysUser::getId);
        return sysUserMapper.selectList(wrapper).stream()
                .map(user -> AdminUserView.builder()
                        .userId(user.getId())
                        .username(user.getUsername())
                        .displayName(user.getDisplayName())
                        .userType(UserType.ADMIN)
                        .deptId(user.getDeptId())
                        .status(user.getStatus())
                        .build())
                .toList();
    }
}
