package com.supplychain.service.api.customer.view;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 客户列表视图。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerView implements Serializable {

    /**
     * 客户 ID。
     */
    private Long customerId;

    /**
     * 客户编码。
     */
    private String customerCode;

    /**
     * 客户名称。
     */
    private String customerName;

    /**
     * 客户类型。
     */
    private String customerType;

    /**
     * 客户等级。
     */
    private String customerLevel;

    /**
     * 销售负责人 ID。
     */
    private Long salesOwnerId;

    /**
     * 客户状态。
     */
    private String status;

    /**
     * 是否启用授信。
     */
    private Integer creditEnabled;

    /**
     * 授信额度。
     */
    private BigDecimal creditLimit;

    /**
     * 结算方式。
     */
    private String settlementType;

    /**
     * 备注。
     */
    private String remark;

    /**
     * 联系人数。
     */
    private Integer contactCount;

    /**
     * 地址数。
     */
    private Integer addressCount;

    /**
     * App 账号数。
     */
    private Integer appAccountCount;

    /**
     * 启用中的 App 账号数。
     */
    private Integer enabledAppAccountCount;
}
