package com.supplychain.service.api.admin.user.query;

import lombok.Data;

import java.io.Serializable;

/**
 * 后台用户查询条件
 */
@Data
public class AdminUserQuery implements Serializable {

    /**
     * 用户名或显示名称关键字
     */
    private String keyword;
}
