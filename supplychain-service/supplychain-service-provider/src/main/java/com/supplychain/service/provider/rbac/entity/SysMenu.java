package com.supplychain.service.provider.rbac.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.supplychain.common.mybatis.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 菜单权限实体
 */
@Data
@TableName("sys_menu")
@EqualsAndHashCode(callSuper = true)
public class SysMenu extends BaseEntity {

    /**
     * 菜单名称
     */
    private String menuName;

    /**
     * 权限编码
     */
    private String permissionCode;

    /**
     * 状态，1 启用，0 禁用
     */
    private Integer status;
}
