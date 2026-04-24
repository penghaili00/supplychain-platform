package com.supplychain.service.api.rbac.menu.command;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;

/**
 * 创建菜单权限命令
 */
@Data
public class MenuCreateCommand implements Serializable {

    /**
     * 父级菜单 ID，0 表示顶级菜单
     */
    private Long parentId;

    /**
     * 菜单名称
     */
    @NotBlank(message = "菜单名称不能为空")
    private String menuName;

    /**
     * 权限编码
     */
    private String permissionCode;

    /**
     * 状态，1 启用，0 禁用
     */
    private Integer status;
}
