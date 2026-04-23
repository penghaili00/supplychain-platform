package com.supplychain.service.api.admin.user.command;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 创建后台用户命令
 */
@Data
public class AdminUserCreateCommand implements Serializable {

    /**
     * 登录账号
     */
    @NotBlank(message = "用户名不能为空")
    private String username;

    /**
     * 登录密码
     */
    @NotBlank(message = "密码不能为空")
    private String password;

    /**
     * 显示名称
     */
    @NotBlank(message = "显示名称不能为空")
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

    /**
     * 角色 ID 集合
     */
    private List<Long> roleIds;
}
