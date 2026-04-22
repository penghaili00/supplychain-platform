package com.supplychain.common.core.enums.customer;

import com.supplychain.common.core.enums.CodeEnum;
import lombok.Getter;

/**
 * 客户类型。
 */
@Getter
public enum CustomerType implements CodeEnum {
    ENTERPRISE("ENTERPRISE", "企业客户");

    private final String code;
    private final String desc;

    CustomerType(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static CustomerType fromCode(String code) {
        return CodeEnum.fromCode(CustomerType.class, code);
    }
}
