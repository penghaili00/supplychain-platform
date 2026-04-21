package com.supplychain.common.core.domain;

import com.supplychain.common.core.enums.DataScopeType;
import com.supplychain.common.core.enums.UserType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 当前登录会话用户
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SessionUser implements Serializable {

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
     * 会话 ID
     */
    private String sessionId;

    /**
     * 数据权限范围
     */
    private DataScopeType dataScopeType;

    /**
     * 角色编码集合
     */
    @Builder.Default
    private List<String> roles = new ArrayList<>();

    /**
     * 权限编码集合
     */
    @Builder.Default
    private List<String> permissions = new ArrayList<>();

    public boolean isAdmin() {
        return UserType.ADMIN == userType;
    }
}
