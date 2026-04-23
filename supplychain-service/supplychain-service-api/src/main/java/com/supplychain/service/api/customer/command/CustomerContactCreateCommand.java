package com.supplychain.service.api.customer.command;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;

/**
 * 创建客户联系人命令。
 */
@Data
public class CustomerContactCreateCommand implements Serializable {

    @NotBlank(message = "联系人姓名不能为空")
    private String contactName;

    private String mobile;

    private String phone;

    private String email;

    private Integer isDefault;

    private String status;

    private String remark;
}
