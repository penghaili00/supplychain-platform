package com.supplychain.service.provider.order.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.supplychain.common.mybatis.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 订单明细实体
 */
@Data
@TableName("sc_order_item")
@EqualsAndHashCode(callSuper = true)
public class OrderItem extends BaseEntity {

    /**
     * 订单id
     */
    private Long orderId;

    /**
     * sku id
     */
    private Long skuId;

    /**
     * sku编码
     */
    private String skuCode;

    /**
     * sku名称
     */
    private String skuName;

    /**
     * 销售单位
     */
    private String saleUnit;

    /**
     * 销售单价
     */
    private BigDecimal salePrice;

    /**
     * 下单数量
     */
    private BigDecimal orderQty;

    /**
     * 已锁定数量
     */
    private BigDecimal lockedQty;

    /**
     * 已发货数量
     */
    private BigDecimal shippedQty;

    /**
     * 已收货数量
     */
    private BigDecimal receivedQty;

    /**
     * 行金额
     */
    private BigDecimal lineAmount;

    /**
     * 分配仓库id
     */
    private Long warehouseId;

    /**
     * 备注
     */
    private String remark;
}
