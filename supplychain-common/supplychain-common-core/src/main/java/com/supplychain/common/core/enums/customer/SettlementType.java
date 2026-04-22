package com.supplychain.common.core.enums.customer;

import com.supplychain.common.core.enums.CodeEnum;
import lombok.Getter;

/**
 * 结算方式。
 */
@Getter
public enum SettlementType implements CodeEnum {
    CASH("CASH", "现款现结");

    private final String code;
    private final String desc;

    SettlementType(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static SettlementType fromCode(String code) {
        return CodeEnum.fromCode(SettlementType.class, code);
    }
}
