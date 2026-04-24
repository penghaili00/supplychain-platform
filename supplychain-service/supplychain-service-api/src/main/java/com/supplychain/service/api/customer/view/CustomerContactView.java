package com.supplychain.service.api.customer.view;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 客户联系人视图。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerContactView implements Serializable {

    /**
     * 联系人 ID。
     */
    private Long contactId;

    /**
     * 客户 ID。
     */
    private Long customerId;

    /**
     * 联系人姓名。
     */
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
