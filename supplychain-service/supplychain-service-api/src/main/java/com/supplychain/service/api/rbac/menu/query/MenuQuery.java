package com.supplychain.service.api.rbac.menu.query;

import lombok.Data;

import java.io.Serializable;

/**
 * 菜单权限查询条件
 */
@Data
public class MenuQuery implements Serializable {

    /**
     * 菜单名称或权限编码关键字
     */
    private String keyword;

    /**
     * 状态，1 启用，0 禁用
     */
    private Integer status;
}
