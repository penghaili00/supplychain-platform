package com.supplychain.service.api.rbac.role.command;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 更新角色命令
 */
@Data
public class RoleUpdateCommand implements Serializable {

    /**
     * 角色编码
     */
    @NotBlank(message = "角色编码不能为空")
    private String roleKey;

    /**
     * 角色名称
     */
    @NotBlank(message = "角色名称不能为空")
    private String roleName;

    /**
     * 数据权限范围
     */
    @NotBlank(message = "数据权限范围不能为空")
    private String dataScope;

    /**
     * 状态，1 启用，0 禁用
     */
    private Integer status;

    /**
     * 绑定菜单 ID 集合
     */
    private List<Long> menuIds;
}
