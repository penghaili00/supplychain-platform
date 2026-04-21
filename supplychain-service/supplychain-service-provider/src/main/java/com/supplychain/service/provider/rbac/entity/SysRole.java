package com.supplychain.service.provider.rbac.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.supplychain.common.mybatis.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 角色实体
 */
@Data
@TableName("sys_role")
@EqualsAndHashCode(callSuper = true)
public class SysRole extends BaseEntity {

    /**
     * 角色编码
     */
    private String roleKey;

    /**
     * 角色名称
     */
    private String roleName;

    /**
     * 数据权限范围编码
     */
    private String dataScope;

    /**
     * 状态，1 启用，0 禁用
     */
    private Integer status;
}
