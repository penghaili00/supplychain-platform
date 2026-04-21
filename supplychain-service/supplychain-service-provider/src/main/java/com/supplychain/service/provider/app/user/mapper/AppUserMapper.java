package com.supplychain.service.provider.app.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.supplychain.service.provider.app.user.entity.AppUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface AppUserMapper extends BaseMapper<AppUser> {

    @Select("""
            select *
            from app_user
            where username = #{username}
              and deleted = 0
            limit 1
            """)
    AppUser selectByUsername(@Param("username") String username);

    @Select("""
            select *
            from app_user
            where id = #{userId}
              and deleted = 0
            limit 1
            """)
    AppUser selectByUserId(@Param("userId") Long userId);
}
