package com.supplychain.service.api.customer.query;

import lombok.Data;

import java.io.Serializable;

/**
 * 客户查询条件。
 */
@Data
public class CustomerQuery implements Serializable {

    /**
     * 客户编码或客户名称关键字。
     */
    private String keyword;

    /**
     * 客户状态。
     */
    private String status;
}
