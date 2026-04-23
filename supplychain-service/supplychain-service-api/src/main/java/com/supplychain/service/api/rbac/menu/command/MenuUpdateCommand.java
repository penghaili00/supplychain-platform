package com.supplychain.service.api.rbac.menu.command;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;

/**
 * 更新菜单权限命令
 */
@Data
public class MenuUpdateCommand implements Serializable {

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
