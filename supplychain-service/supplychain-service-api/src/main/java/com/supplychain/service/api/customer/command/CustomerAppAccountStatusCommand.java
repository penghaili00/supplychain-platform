package com.supplychain.service.api.customer.command;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

/**
 * 客户 App 账号状态更新命令。
 */
@Data
public class CustomerAppAccountStatusCommand implements Serializable {

    @NotNull(message = "状态不能为空")
    private Integer status;
}
