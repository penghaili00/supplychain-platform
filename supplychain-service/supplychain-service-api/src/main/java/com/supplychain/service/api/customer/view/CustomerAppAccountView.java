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

    /**
     * App 用户 ID。
     */
    private Long userId;

    /**
     * 客户 ID。
     */
    private Long customerId;

    /**
     * 登录账号。
     */
    private String username;

    /**
     * 显示名称。
     */
    private String displayName;

    /**
     * 账号状态，1 启用，0 禁用。
     */
    private Integer status;

    /**
     * 角色 ID 集合。
     */
    private List<Long> roleIds;

    /**
     * 角色编码集合。
     */
    private List<String> roleKeys;

    /**
     * 角色名称集合。
     */
    private List<String> roleNames;
}
