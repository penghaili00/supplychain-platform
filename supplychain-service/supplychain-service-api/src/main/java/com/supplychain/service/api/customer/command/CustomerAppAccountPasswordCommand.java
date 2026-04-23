package com.supplychain.service.api.customer.command;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;

/**
 * 客户 App 账号重置密码命令。
 */
@Data
public class CustomerAppAccountPasswordCommand implements Serializable {

    @NotBlank(message = "密码不能为空")
    private String password;
}
