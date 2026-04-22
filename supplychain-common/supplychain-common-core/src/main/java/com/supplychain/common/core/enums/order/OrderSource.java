package com.supplychain.common.core.enums.order;

import com.supplychain.common.core.enums.CodeEnum;
import lombok.Getter;

/**
 * 订单来源。
 */
@Getter
public enum OrderSource implements CodeEnum {
    APP("APP", "App下单");

    private final String code;
    private final String desc;

    OrderSource(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static OrderSource fromCode(String code) {
        return CodeEnum.fromCode(OrderSource.class, code);
    }
}
