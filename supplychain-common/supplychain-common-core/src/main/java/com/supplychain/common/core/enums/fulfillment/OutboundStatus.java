package com.supplychain.common.core.enums.fulfillment;

import com.supplychain.common.core.enums.CodeEnum;
import lombok.Getter;

/**
 * 出库单状态。
 */
@Getter
public enum OutboundStatus implements CodeEnum {
    CREATED("CREATED", "已创建"),
    PROCESSING("PROCESSING", "处理中"),
    DONE("DONE", "已完成"),
    CANCELED("CANCELED", "已取消");

    private final String code;
    private final String desc;

    OutboundStatus(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static OutboundStatus fromCode(String code) {
        return CodeEnum.fromCode(OutboundStatus.class, code);
    }
}
