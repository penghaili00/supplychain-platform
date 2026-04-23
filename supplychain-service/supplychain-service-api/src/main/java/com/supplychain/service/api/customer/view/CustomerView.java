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

    private Integer contactCount;

    private Integer addressCount;

    private Integer appAccountCount;

    private Integer enabledAppAccountCount;
}
