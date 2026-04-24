package com.supplychain.service.api.customer.command;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;

/**
 * 更新客户联系人命令。
 */
@Data
public class CustomerContactUpdateCommand implements Serializable {

    /**
     * 联系人姓名。
     */
    @NotBlank(message = "联系人姓名不能为空")
    private String contactName;

    /**
     * 手机号。
     */
    private String mobile;

    /**
     * 座机号。
     */
    private String phone;

    /**
     * 邮箱。
     */
    private String email;

    /**
     * 是否默认联系人。
     */
    private Integer isDefault;

    /**
     * 联系人状态。
     */
    private String status;

    /**
     * 备注。
     */
    private String remark;
}
