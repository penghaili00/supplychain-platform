package com.supplychain.service.api.customer.command;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;

/**
 * 客户状态更新命令。
 */
@Data
public class CustomerStatusCommand implements Serializable {

    /**
     * 客户状态。
     */
    @NotBlank(message = "状态不能为空")
    private String status;
}
