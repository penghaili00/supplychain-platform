package com.supplychain.service.api.customer.view;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 客户 App 账号视图。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerAppAccountView implements Serializable {

    private Long userId;

    private Long customerId;

    private String username;

    private String displayName;

    private Integer status;

    private List<Long> roleIds;

    private List<String> roleKeys;

    private List<String> roleNames;
}
