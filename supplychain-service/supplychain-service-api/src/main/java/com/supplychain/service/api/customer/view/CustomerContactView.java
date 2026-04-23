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

    private Long contactId;

    private Long customerId;

    private String contactName;

    private String mobile;

    private String phone;

    private String email;

    private Integer isDefault;

    private String status;

    private String remark;
}
