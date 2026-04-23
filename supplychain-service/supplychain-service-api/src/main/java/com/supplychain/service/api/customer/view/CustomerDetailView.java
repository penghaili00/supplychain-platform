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

    private Long customerId;

    private String customerCode;

    private String customerName;

    private String customerType;

    private String customerLevel;

    private Long salesOwnerId;

    private String status;

    private Integer creditEnabled;

    private BigDecimal creditLimit;

    private String settlementType;

    private String remark;

    private List<CustomerContactView> contacts;

    private List<CustomerAddressView> addresses;

    private List<CustomerAppAccountView> appAccounts;
}
