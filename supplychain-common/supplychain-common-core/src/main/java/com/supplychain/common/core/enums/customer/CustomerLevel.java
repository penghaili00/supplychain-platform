package com.supplychain.common.core.enums.customer;

import com.supplychain.common.core.enums.CodeEnum;
import lombok.Getter;

/**
 * 客户等级。
 */
@Getter
public enum CustomerLevel implements CodeEnum {
    NORMAL("NORMAL", "普通客户");

    private final String code;
    private final String desc;

    CustomerLevel(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static CustomerLevel fromCode(String code) {
        return CodeEnum.fromCode(CustomerLevel.class, code);
    }
}
