package com.supplychain.service.provider.rbac.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.supplychain.service.provider.rbac.entity.AppUserRole;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AppUserRoleMapper extends BaseMapper<AppUserRole> {
}
