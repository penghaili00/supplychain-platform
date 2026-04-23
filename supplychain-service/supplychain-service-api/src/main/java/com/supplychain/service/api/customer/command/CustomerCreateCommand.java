package com.supplychain.service.api.customer.command;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 创建客户命令。
 */
@Data
public class CustomerCreateCommand implements Serializable {

    @NotBlank(message = "客户编码不能为空")
    private String customerCode;

    @NotBlank(message = "客户名称不能为空")
    private String customerName;

    private String customerType;

    private String customerLevel;

    private Long salesOwnerId;

    private String status;

    private Integer creditEnabled;

    private BigDecimal creditLimit;

    private String settlementType;

    private String remark;
}
