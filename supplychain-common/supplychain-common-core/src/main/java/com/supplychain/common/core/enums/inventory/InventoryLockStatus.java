package com.supplychain.common.core.enums.inventory;

import com.supplychain.common.core.enums.CodeEnum;
import lombok.Getter;

/**
 * 库存锁定状态。
 */
@Getter
public enum InventoryLockStatus implements CodeEnum {
    LOCKED("LOCKED", "已锁定"),
    RELEASED("RELEASED", "已释放"),
    DEDUCTED("DEDUCTED", "已扣减");

    private final String code;
    private final String desc;

    InventoryLockStatus(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static InventoryLockStatus fromCode(String code) {
        return CodeEnum.fromCode(InventoryLockStatus.class, code);
    }
}
