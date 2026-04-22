package com.supplychain.common.core.enums.fulfillment;

import com.supplychain.common.core.enums.CodeEnum;
import lombok.Getter;

/**
 * 发货单状态。
 */
@Getter
public enum ShipmentStatus implements CodeEnum {
    WAIT_SHIPMENT("WAIT_SHIPMENT", "待登记发货"),
    SHIPPED("SHIPPED", "已发货"),
    SIGNED("SIGNED", "已签收"),
    CLOSED("CLOSED", "已关闭");

    private final String code;
    private final String desc;

    ShipmentStatus(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static ShipmentStatus fromCode(String code) {
        return CodeEnum.fromCode(ShipmentStatus.class, code);
    }
}
