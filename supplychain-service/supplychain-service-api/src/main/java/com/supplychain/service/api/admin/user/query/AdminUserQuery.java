package com.supplychain.service.api.admin.user.query;

import com.supplychain.common.core.domain.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 后台用户查询条件
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AdminUserQuery extends PageQuery {

    /**
     * 用户名或显示名称关键字
     */
    private String keyword;

    /**
     * 状态，1 启用，0 禁用
     */
    private Integer status;
}
