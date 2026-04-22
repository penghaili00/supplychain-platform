package com.supplychain.common.core.enums.warehouse;

import com.supplychain.common.core.enums.CodeEnum;
import lombok.Getter;

/**
 * 仓库类型。
 */
@Getter
public enum WarehouseType implements CodeEnum {
    SELF("SELF", "自营仓");

    private final String code;
    private final String desc;

    WarehouseType(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static WarehouseType fromCode(String code) {
        return CodeEnum.fromCode(WarehouseType.class, code);
    }
}
