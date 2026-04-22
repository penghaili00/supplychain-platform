package com.supplychain.service.provider.inventory.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.supplychain.common.core.enums.inventory.InventoryBizType;
import com.supplychain.common.mybatis.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 库存流水实体
 */
@Data
@TableName("sc_inventory_log")
@EqualsAndHashCode(callSuper = true)
public class InventoryLog extends BaseEntity {

    /**
     * 仓库ID
     */
    private Long warehouseId;

    /**
     * SKU ID
     */
    private Long skuId;

    /**
     * 业务类型
     */
    private InventoryBizType bizType;

    /**
     * 业务单号
     */
    private String bizNo;

    /**
     * 变动数量
     */
    private BigDecimal changeQty;

    /**
     * 变动前可售库存
     */
    private BigDecimal beforeAvailableQty;

    /**
     * 变动后可售库存
     */
    private BigDecimal afterAvailableQty;

    /**
     * 变动前锁定库存
     */
    private BigDecimal beforeLockedQty;

    /**
     * 变动后锁定库存
     */
    private BigDecimal afterLockedQty;

    /**
     * 备注
     */
    private String remark;

    /**
     * 操作时间
     */
    private LocalDateTime operateTime;
}
