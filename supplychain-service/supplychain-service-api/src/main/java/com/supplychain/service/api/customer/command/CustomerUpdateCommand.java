package com.supplychain.service.api.customer.command;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 更新客户命令。
 */
@Data
public class CustomerUpdateCommand implements Serializable {

    /**
     * 客户编码。
     */
    @NotBlank(message = "客户编码不能为空")
    private String customerCode;

    /**
     * 客户名称。
     */
    @NotBlank(message = "客户名称不能为空")
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
}
