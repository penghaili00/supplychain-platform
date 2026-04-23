package com.supplychain.service.provider.order.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.supplychain.common.core.enums.fulfillment.FulfillmentStatus;
import com.supplychain.common.core.enums.order.AfterSaleStatus;
import com.supplychain.common.core.enums.order.AuditStatus;
import com.supplychain.common.core.enums.order.OrderSource;
import com.supplychain.common.core.enums.order.OrderStatus;
import com.supplychain.common.core.enums.order.SettlementStatus;
import com.supplychain.common.mybatis.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单主表实体
 */
@Data
@TableName("sc_order_main")
@EqualsAndHashCode(callSuper = true)
public class OrderMain extends BaseEntity {

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 父订单id
     */
    private Long parentOrderId;

    /**
     * 客户id
     */
    private Long customerId;

    /**
     * 客户名称
     */
    private String customerName;

    /**
     * 订单来源
     */
    private OrderSource orderSource;

    /**
     * 订单主状态
     */
    private OrderStatus orderStatus;

    /**
     * 审核状态
     */
    private AuditStatus auditStatus;

    /**
     * 履约状态
     */
    private FulfillmentStatus fulfillmentStatus;

    /**
     * 结算状态
     */
    private SettlementStatus settlementStatus;

    /**
     * 售后状态
     */
    private AfterSaleStatus afterSaleStatus;

    /**
     * 订单总金额
     */
    private BigDecimal totalAmount;

    /**
     * 优惠金额
     */
    private BigDecimal discountAmount;

    /**
     * 应付金额
     */
    private BigDecimal payableAmount;

    /**
     * 收货人姓名
     */
    private String receiverName;

    /**
     * 收货人电话
     */
    private String receiverPhone;

    /**
     * 收货人地址
     */
    private String receiverAddress;

    /**
     * 订单备注
     */
    private String remark;

    /**
     * 订单提交时间
     */
    private LocalDateTime submitTime;

    /**
     * 订单审核时间
     */
    private LocalDateTime auditTime;

    /**
     * 订单完成时间
     */
    private LocalDateTime completeTime;
}
