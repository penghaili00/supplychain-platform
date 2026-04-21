package com.supplychain.service.provider.admin.user.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.supplychain.common.mybatis.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 后台用户实体
 */
@Data
@TableName("sys_user")
@EqualsAndHashCode(callSuper = true)
public class SysUser extends BaseEntity {

    /**
     * 登录账号
     */
    private String username;

    /**
     * 登录密码
     */
    private String password;

    /**
     * 显示名称
     */
    private String displayName;

    /**
     * 部门 ID
     */
    private Long deptId;

    /**
     * 部门祖级路径
     */
    private String deptAncestors;

    /**
     * 状态，1 启用，0 禁用
     */
    private Integer status;
}
