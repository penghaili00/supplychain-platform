package com.supplychain.service.api.dto;

import com.supplychain.common.core.enums.DataScopeType;
import com.supplychain.common.core.enums.UserType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 用户资料视图
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileView implements Serializable {

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
     * 数据权限范围
     */
    private DataScopeType dataScopeType;

    /**
     * 角色编码集合
     */
    private List<String> roles;

    /**
     * 权限编码集合
     */
    private List<String> permissions;
}
