package com.supplychain.service.provider.rbac.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.supplychain.service.provider.rbac.entity.SysMenu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SysMenuMapper extends BaseMapper<SysMenu> {

    @Select("""
            select distinct m.permission_code
            from sys_menu m
                     join sys_role_menu rm on rm.menu_id = m.id
                     join sys_user_role ur on ur.role_id = rm.role_id
            where ur.user_id = #{userId}
              and m.deleted = 0
              and m.status = 1
              and m.permission_code is not null
              and m.permission_code <> ''
            """)
    List<String> listPermissionCodesByAdminUserId(@Param("userId") Long userId);

    @Select("""
            select distinct m.permission_code
            from sys_menu m
                     join sys_role_menu rm on rm.menu_id = m.id
                     join app_user_role ur on ur.role_id = rm.role_id
            where ur.user_id = #{userId}
              and m.deleted = 0
              and m.status = 1
              and m.permission_code is not null
              and m.permission_code <> ''
            """)
    List<String> listPermissionCodesByAppUserId(@Param("userId") Long userId);
}
