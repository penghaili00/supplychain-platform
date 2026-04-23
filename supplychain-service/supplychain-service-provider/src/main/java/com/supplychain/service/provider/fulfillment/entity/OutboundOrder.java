package com.supplychain.service.provider.fulfillment.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.supplychain.common.core.enums.fulfillment.OutboundStatus;
import com.supplychain.common.mybatis.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 出库单实体
 */
@Data
@TableName("sc_outbound_order")
@EqualsAndHashCode(callSuper = true)
public class OutboundOrder extends BaseEntity {

    /**
     * 出库单号
     */
    private String outboundNo;

    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 仓库ID
     */
    private Long warehouseId;

    /**
     * 出库状态
     */
    private OutboundStatus outboundStatus;

    /**
     * 拣货时间
     */
    private LocalDateTime pickTime;

    /**
     * 复核时间
     */
    private LocalDateTime reviewTime;

    /**
     * 出库时间
     */
    private LocalDateTime outboundTime;

    /**
     * 备注
     */
    private String remark;
}
