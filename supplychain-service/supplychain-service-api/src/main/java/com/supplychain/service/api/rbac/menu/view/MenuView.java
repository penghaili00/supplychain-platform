package com.supplychain.service.api.rbac.menu.view;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 菜单权限视图
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuView implements Serializable {

    /**
     * 菜单 ID
     */
    private Long menuId;

    /**
     * 菜单名称
     */
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
