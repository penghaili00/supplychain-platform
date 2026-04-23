package com.supplychain.service.api.admin.user.command;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

/**
 * 更新后台用户状态命令
 */
@Data
public class AdminUserStatusCommand implements Serializable {

    /**
     * 状态，1 启用，0 禁用
     */
    @NotNull(message = "状态不能为空")
    private Integer status;
}
