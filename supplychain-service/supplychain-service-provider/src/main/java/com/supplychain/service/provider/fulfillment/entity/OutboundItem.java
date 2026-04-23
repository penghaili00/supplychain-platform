package com.supplychain.service.provider.fulfillment.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.supplychain.common.mybatis.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 出库单明细实体
 */
@Data
@TableName("sc_outbound_item")
@EqualsAndHashCode(callSuper = true)
public class OutboundItem extends BaseEntity {

    /**
     * 出库单ID
     */
    private Long outboundId;

    /**
     * 订单明细ID
     */
    private Long orderItemId;

    /**
     * SKU ID
     */
    private Long skuId;

    /**
     * 计划出库数量
     */
    private BigDecimal planQty;

    /**
     * 实际出库数量
     */
    private BigDecimal actualQty;

    /**
     * 备注
     */
    private String remark;
}
