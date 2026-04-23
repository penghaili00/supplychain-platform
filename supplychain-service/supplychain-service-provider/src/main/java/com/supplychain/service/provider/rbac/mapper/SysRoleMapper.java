package com.supplychain.service.provider.rbac.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.supplychain.service.provider.rbac.entity.SysRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SysRoleMapper extends BaseMapper<SysRole> {

    @Select("""
            select r.*
            from sys_role r
                     join sys_user_role ur on ur.role_id = r.id
            where ur.user_id = #{userId}
              and r.deleted = 0
              and r.status = 1
            """)
    List<SysRole> listByAdminUserId(@Param("userId") Long userId);

    @Select("""
            select r.*
            from sys_role r
                     join app_user_role ur on ur.role_id = r.id
            where ur.user_id = #{userId}
              and r.deleted = 0
              and r.status = 1
            """)
    List<SysRole> listByAppUserId(@Param("userId") Long userId);

    @Select("""
            select *
            from sys_role
            where role_key = #{roleKey}
              and deleted = 0
            limit 1
            """)
    SysRole selectByRoleKey(@Param("roleKey") String roleKey);
}
