package com.supplychain.common.core.enums.order;

import com.supplychain.common.core.enums.CodeEnum;
import lombok.Getter;

/**
 * 售后状态。
 */
@Getter
public enum AfterSaleStatus implements CodeEnum {
    NONE("NONE", "无售后"),
    APPLYING("APPLYING", "申请中"),
    PROCESSING("PROCESSING", "处理中"),
    FINISHED("FINISHED", "处理完成"),
    CLOSED("CLOSED", "已关闭");

    private final String code;
    private final String desc;

    AfterSaleStatus(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static AfterSaleStatus fromCode(String code) {
        return CodeEnum.fromCode(AfterSaleStatus.class, code);
    }
}
