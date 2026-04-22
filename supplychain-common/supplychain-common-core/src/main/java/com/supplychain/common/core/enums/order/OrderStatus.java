package com.supplychain.common.core.enums.order;

import com.supplychain.common.core.enums.CodeEnum;
import lombok.Getter;

/**
 * 订单主状态。
 */
@Getter
public enum OrderStatus implements CodeEnum {
    DRAFT("DRAFT", "草稿"),
    PENDING_AUDIT("PENDING_AUDIT", "待审核"),
    AUDIT_REJECTED("AUDIT_REJECTED", "审核驳回"),
    PENDING_LOCK("PENDING_LOCK", "待锁库"),
    LOCK_FAILED("LOCK_FAILED", "锁库失败"),
    PENDING_OUTBOUND("PENDING_OUTBOUND", "待出库"),
    PENDING_SHIPMENT("PENDING_SHIPMENT", "待发货"),
    PENDING_RECEIPT("PENDING_RECEIPT", "待收货"),
    COMPLETED("COMPLETED", "已完成"),
    CANCELED("CANCELED", "已取消");

    private final String code;
    private final String desc;

    OrderStatus(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static OrderStatus fromCode(String code) {
        return CodeEnum.fromCode(OrderStatus.class, code);
    }
}
