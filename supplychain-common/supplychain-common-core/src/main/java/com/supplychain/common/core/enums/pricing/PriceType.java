package com.supplychain.common.core.enums.pricing;

import com.supplychain.common.core.enums.CodeEnum;
import lombok.Getter;

/**
 * 一期价格类型。
 */
@Getter
public enum PriceType implements CodeEnum {
    BASE("BASE", "基础价"),
    CUSTOMER("CUSTOMER", "客户专属价");

    private final String code;
    private final String desc;

    PriceType(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static PriceType fromCode(String code) {
        return CodeEnum.fromCode(PriceType.class, code);
    }
}
