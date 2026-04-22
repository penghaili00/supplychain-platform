package com.supplychain.common.core.enums.common;

import com.supplychain.common.core.enums.CodeEnum;
import lombok.Getter;

/**
 * 通用启停状态，适用于一期大部分 status 字段。
 */
@Getter
public enum EnableStatus implements CodeEnum {
    ENABLED("ENABLED", "启用"),
    DISABLED("DISABLED", "停用");

    private final String code;
    private final String desc;

    EnableStatus(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static EnableStatus fromCode(String code) {
        return CodeEnum.fromCode(EnableStatus.class, code);
    }
}
