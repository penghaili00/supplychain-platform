package com.supplychain.service.provider.app.user.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.supplychain.common.mybatis.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * App 端用户实体
 */
@Data
@TableName("app_user")
@EqualsAndHashCode(callSuper = true)
public class AppUser extends BaseEntity {

    /**
     * 登录账号
     */
    private String username;

    /**
     * 密码哈希
     */
    @TableField("password_hash")
    private String passwordHash;

    /**
     * 密码盐值
     */
    @TableField("password_salt")
    private String passwordSalt;

    /**
     * 显示名称
     */
    private String displayName;

    /**
     * 状态，1 启用，0 禁用
     */
    private Integer status;
}
