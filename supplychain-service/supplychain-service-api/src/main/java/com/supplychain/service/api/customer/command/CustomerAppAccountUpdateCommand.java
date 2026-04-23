package com.supplychain.service.api.customer.command;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 更新客户 App 账号命令。
 */
@Data
public class CustomerAppAccountUpdateCommand implements Serializable {

    @NotBlank(message = "账号不能为空")
    private String username;

    @NotBlank(message = "显示名称不能为空")
    private String displayName;

    private Integer status;

    private List<Long> roleIds;
}
