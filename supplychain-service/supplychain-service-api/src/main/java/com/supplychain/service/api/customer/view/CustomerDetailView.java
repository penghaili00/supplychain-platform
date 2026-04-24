package com.supplychain.service.api.customer.view;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 客户详情视图。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDetailView implements Serializable {

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
     * 联系人列表。
     */
    private List<CustomerContactView> contacts;

    /**
     * 地址列表。
     */
    private List<CustomerAddressView> addresses;

    /**
     * App 账号列表。
     */
    private List<CustomerAppAccountView> appAccounts;
}
