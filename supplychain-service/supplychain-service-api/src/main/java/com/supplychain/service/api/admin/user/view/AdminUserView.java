package com.supplychain.service.api.admin.user.view;

import com.supplychain.common.core.enums.UserType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 后台用户视图
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminUserView implements Serializable {

    /**
     * 用户 ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 显示名称
     */
    private String displayName;

    /**
     * 用户类型
     */
    private UserType userType;

    /**
     * 部门 ID
     */
    private Long deptId;

    /**
     * 状态，1 启用，0 禁用
     */
    private Integer status;

    /**
     * 角色 ID 集合
     */
    private List<Long> roleIds;

    /**
     * 角色编码集合
     */
    private List<String> roleKeys;

    /**
     * 角色名称集合
     */
    private List<String> roleNames;
}
