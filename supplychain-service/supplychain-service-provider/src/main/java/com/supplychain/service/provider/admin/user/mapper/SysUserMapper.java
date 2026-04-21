package com.supplychain.service.provider.admin.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.supplychain.service.provider.admin.user.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {

    @Select("""
            select *
            from sys_user
            where username = #{username}
              and deleted = 0
            limit 1
            """)
    SysUser selectByUsername(@Param("username") String username);

    @Select("""
            select *
            from sys_user
            where id = #{userId}
              and deleted = 0
            limit 1
            """)
    SysUser selectByUserId(@Param("userId") Long userId);
}
