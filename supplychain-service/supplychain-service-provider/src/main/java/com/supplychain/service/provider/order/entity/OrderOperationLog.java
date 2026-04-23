package com.supplychain.service.provider.order.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.supplychain.common.core.enums.order.OrderOperationType;
import com.supplychain.common.core.enums.order.OrderOperatorType;
import com.supplychain.common.core.enums.order.OrderStatus;
import com.supplychain.common.mybatis.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 订单业务轨迹实体
 */
@Data
@TableName("sc_order_operation_log")
@EqualsAndHashCode(callSuper = true)
public class OrderOperationLog extends BaseEntity {

    /**
     * 订单id
     */
    private Long orderId;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 订单操作类型
     */
    private OrderOperationType operationType;

    /**
     * 操作人id
     */
    private Long operatorId;

    /**
     * 操作人名称
     */
    private String operatorName;

    /**
     * 操作人类型
     */
    private OrderOperatorType operatorType;

    /**
     * 变更前订单状态
     */
    private OrderStatus beforeStatus;

    /**
     * 变更后订单状态
     */
    private OrderStatus afterStatus;

    /**
     * 备注
     */
    private String remark;

    /**
     * 操作时间
     */
    private LocalDateTime operateTime;
}
