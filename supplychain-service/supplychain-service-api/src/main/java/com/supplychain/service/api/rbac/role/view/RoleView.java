package com.supplychain.service.api.rbac.role.view;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 角色视图
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleView implements Serializable {

    /**
     * 角色 ID
     */
    private Long roleId;

    /**
     * 角色编码
     */
    private String roleKey;

    /**
     * 角色名称
     */
    private String roleName;

    /**
     * 数据权限范围
     */
    private String dataScope;

    /**
     * 状态，1 启用，0 禁用
     */
    private Integer status;

    /**
     * 菜单 ID 集合
     */
    private List<Long> menuIds;

    /**
     * 菜单名称集合
     */
    private List<String> menuNames;

    /**
     * 权限编码集合
     */
    private List<String> permissionCodes;
}
