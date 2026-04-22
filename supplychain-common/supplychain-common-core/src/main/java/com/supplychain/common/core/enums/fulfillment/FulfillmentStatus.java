package com.supplychain.common.core.enums.fulfillment;

import com.supplychain.common.core.enums.CodeEnum;
import lombok.Getter;

/**
 * 履约状态。
 */
@Getter
public enum FulfillmentStatus implements CodeEnum {
    NONE("NONE", "未履约"),
    LOCKING("LOCKING", "锁库处理中"),
    LOCKED("LOCKED", "已锁库"),
    OUTBOUNDING("OUTBOUNDING", "出库处理中"),
    WAIT_SHIPMENT("WAIT_SHIPMENT", "待登记发货"),
    SHIPPED("SHIPPED", "已发货"),
    RECEIVED("RECEIVED", "已收货");

    private final String code;
    private final String desc;

    FulfillmentStatus(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static FulfillmentStatus fromCode(String code) {
        return CodeEnum.fromCode(FulfillmentStatus.class, code);
    }
}
