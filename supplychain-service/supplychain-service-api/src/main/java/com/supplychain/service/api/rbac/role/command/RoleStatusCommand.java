package com.supplychain.service.api.rbac.role.command;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

/**
 * 更新角色状态命令
 */
@Data
public class RoleStatusCommand implements Serializable {

    /**
     * 状态，1 启用，0 禁用
     */
    @NotNull(message = "状态不能为空")
    private Integer status;
}
