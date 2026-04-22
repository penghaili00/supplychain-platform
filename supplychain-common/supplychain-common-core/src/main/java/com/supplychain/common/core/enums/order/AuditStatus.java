package com.supplychain.common.core.enums.order;

import com.supplychain.common.core.enums.CodeEnum;
import lombok.Getter;

/**
 * 审核状态。
 */
@Getter
public enum AuditStatus implements CodeEnum {
    NONE("NONE", "未进入审核"),
    PENDING("PENDING", "待审核"),
    APPROVED("APPROVED", "审核通过"),
    REJECTED("REJECTED", "审核驳回");

    private final String code;
    private final String desc;

    AuditStatus(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static AuditStatus fromCode(String code) {
        return CodeEnum.fromCode(AuditStatus.class, code);
    }
}
