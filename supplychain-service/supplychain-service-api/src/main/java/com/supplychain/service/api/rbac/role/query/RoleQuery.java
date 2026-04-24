package com.supplychain.service.api.rbac.role.query;

import com.supplychain.common.core.domain.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 角色查询条件
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class RoleQuery extends PageQuery {

    /**
     * 角色编码或名称关键字
     */
    private String keyword;

    /**
     * 状态，1 启用，0 禁用
     */
    private Integer status;
}
