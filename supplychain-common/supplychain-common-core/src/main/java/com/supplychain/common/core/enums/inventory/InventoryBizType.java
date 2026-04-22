package com.supplychain.common.core.enums.inventory;

import com.supplychain.common.core.enums.CodeEnum;
import lombok.Getter;

/**
 * 库存流水业务类型。
 */
@Getter
public enum InventoryBizType implements CodeEnum {
    LOCK("LOCK", "锁定库存"),
    LOCK_RELEASE("LOCK_RELEASE", "释放锁定库存"),
    OUTBOUND_DEDUCT("OUTBOUND_DEDUCT", "出库扣减库存"),
    MANUAL_ADJUST("MANUAL_ADJUST", "人工调整库存");

    private final String code;
    private final String desc;

    InventoryBizType(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static InventoryBizType fromCode(String code) {
        return CodeEnum.fromCode(InventoryBizType.class, code);
    }
}
