package com.supplychain.service.api.rbac.role.query;

import lombok.Data;

import java.io.Serializable;

/**
 * 角色查询条件
 */
@Data
public class RoleQuery implements Serializable {

    /**
     * 角色编码或名称关键字
     */
    private String keyword;

    /**
     * 状态，1 启用，0 禁用
     */
    private Integer status;
}
