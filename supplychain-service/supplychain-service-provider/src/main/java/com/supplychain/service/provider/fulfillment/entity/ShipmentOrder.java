package com.supplychain.service.provider.fulfillment.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.supplychain.common.core.enums.fulfillment.ShipmentStatus;
import com.supplychain.common.mybatis.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 发货单实体
 */
@Data
@TableName("sc_shipment_order")
@EqualsAndHashCode(callSuper = true)
public class ShipmentOrder extends BaseEntity {

    /**
     * 发货单号
     */
    private String shipmentNo;

    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 出库单ID
     */
    private Long outboundId;

    /**
     * 仓库ID
     */
    private Long warehouseId;

    /**
     * 物流公司
     */
    private String logisticsCompany;

    /**
     * 物流单号
     */
    private String logisticsNo;

    /**
     * 发货状态
     */
    private ShipmentStatus shipmentStatus;

    /**
     * 发货时间
     */
    private LocalDateTime shipTime;

    /**
     * 签收时间
     */
    private LocalDateTime signedTime;

    /**
     * 备注
     */
    private String remark;
}
