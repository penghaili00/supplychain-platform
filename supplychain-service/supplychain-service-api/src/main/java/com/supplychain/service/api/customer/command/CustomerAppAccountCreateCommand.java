package com.supplychain.service.api.customer.command;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 创建客户 App 账号命令。
 */
@Data
public class CustomerAppAccountCreateCommand implements Serializable {

    @NotBlank(message = "账号不能为空")
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;

    @NotBlank(message = "显示名称不能为空")
    private String displayName;

    private Integer status;

    private List<Long> roleIds;
}
