package com.supplychain.common.core.enums.order;

import com.supplychain.common.core.enums.CodeEnum;
import lombok.Getter;

/**
 * 结算状态。
 */
@Getter
public enum SettlementStatus implements CodeEnum {
    NONE("NONE", "未进入结算"),
    UNSETTLED("UNSETTLED", "待结算"),
    PARTIALLY_SETTLED("PARTIALLY_SETTLED", "部分结算"),
    SETTLED("SETTLED", "已结清"),
    CLOSED("CLOSED", "已关闭");

    private final String code;
    private final String desc;

    SettlementStatus(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static SettlementStatus fromCode(String code) {
        return CodeEnum.fromCode(SettlementStatus.class, code);
    }
}
