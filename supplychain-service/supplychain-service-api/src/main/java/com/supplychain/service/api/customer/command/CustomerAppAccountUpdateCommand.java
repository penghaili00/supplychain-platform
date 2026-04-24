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

    /**
     * 登录账号。
     */
    @NotBlank(message = "账号不能为空")
    private String username;

    /**
     * 显示名称。
     */
    @NotBlank(message = "显示名称不能为空")
    private String displayName;

    /**
     * 账号状态，1 启用，0 禁用。
     */
    private Integer status;

    /**
     * 关联角色 ID 集合。
     */
    private List<Long> roleIds;
}
