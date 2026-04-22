package com.supplychain.service.provider.inventory.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.supplychain.common.core.enums.inventory.InventoryLockStatus;
import com.supplychain.common.mybatis.domain.BaseEntity;
import com.supplychain.common.web.jackson.LocalDateTimeMillisSerializer;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 仓库库存锁定实体
 */
@Data
@TableName("sc_inventory_lock")
@EqualsAndHashCode(callSuper = true)
public class InventoryLock extends BaseEntity {

    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 订单明细ID
     */
    private Long orderItemId;

    /**
     * 仓库ID
     */
    private Long warehouseId;

    /**
     * SKU ID
     */
    private Long skuId;

    /**
     * 锁定数量
     */
    private BigDecimal lockQty;

    /**
     * 锁定状态
     */
    private InventoryLockStatus status;

    /**
     * 锁定时间
     */
    @JsonSerialize(using = LocalDateTimeMillisSerializer.class)
    private LocalDateTime lockTime;

    /**
     * 释放时间
     */
    @JsonSerialize(using = LocalDateTimeMillisSerializer.class)
    private LocalDateTime unlockTime;

    /**
     * 扣减时间
     */
    @JsonSerialize(using = LocalDateTimeMillisSerializer.class)
    private LocalDateTime deductTime;

    /**
     * 备注
     */
    private String remark;
}
