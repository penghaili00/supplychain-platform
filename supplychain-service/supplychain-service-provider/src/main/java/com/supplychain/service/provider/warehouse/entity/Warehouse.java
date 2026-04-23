package com.supplychain.service.provider.warehouse.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.supplychain.common.core.enums.common.EnableStatus;
import com.supplychain.common.core.enums.warehouse.WarehouseType;
import com.supplychain.common.mybatis.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 仓库实体
 */
@Data
@TableName("sc_warehouse")
@EqualsAndHashCode(callSuper = true)
public class Warehouse extends BaseEntity {

    /**
     * 仓库编码
     */
    private String warehouseCode;

    /**
     * 仓库名称
     */
    private String warehouseName;

    /**
     * 仓库类型
     */
    private WarehouseType warehouseType;

    /**
     * 区域编码
     */
    private String regionCode;

    /**
     * 履约优先级
     */
    private Integer priority;

    /**
     * 仓库状态
     */
    private EnableStatus status;

    /**
     * 备注
     */
    private String remark;
}
