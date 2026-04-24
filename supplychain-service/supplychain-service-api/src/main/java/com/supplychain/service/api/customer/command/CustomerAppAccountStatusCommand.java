package com.supplychain.service.api.customer.command;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

/**
 * 客户 App 账号状态更新命令。
 */
@Data
public class CustomerAppAccountStatusCommand implements Serializable {

    /**
     * 账号状态，1 启用，0 禁用。
     */
    @NotNull(message = "状态不能为空")
    private Integer status;
}
