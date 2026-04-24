package com.supplychain.service.api.customer.query;

import com.supplychain.common.core.domain.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 客户查询条件。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CustomerQuery extends PageQuery {

    /**
     * 客户编码或客户名称关键字。
     */
    private String keyword;

    /**
     * 客户状态。
     */
    private String status;
}
