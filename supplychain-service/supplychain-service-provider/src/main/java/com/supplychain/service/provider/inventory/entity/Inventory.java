package com.supplychain.service.provider.inventory.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import com.supplychain.common.core.enums.common.EnableStatus;
import com.supplychain.common.mybatis.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 仓库库存实体
 */
@Data
@TableName("sc_inventory")
@EqualsAndHashCode(callSuper = true)
public class Inventory extends BaseEntity {

    /**
     * 仓库ID
     */
    private Long warehouseId;

    /**
     * SKU ID
     */
    private Long skuId;

    /**
     * 在售库存
     */
    private BigDecimal onHandQty;

    /**
     * 可售库存
     */
    private BigDecimal availableQty;

    /**
     * 锁定库存
     */
    private BigDecimal lockedQty;

    /**
     * 安全库存
     */
    private BigDecimal safetyStockQty;

    /**
     * 状态
     */
    private EnableStatus status;

    /**
     * 版本号
     */
    @Version
    private Integer version;

    /**
     * 备注
     */
    private String remark;
}
