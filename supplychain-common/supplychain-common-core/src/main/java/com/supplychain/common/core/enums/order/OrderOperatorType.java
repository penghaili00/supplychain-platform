package com.supplychain.common.core.enums.order;

import com.supplychain.common.core.enums.CodeEnum;
import lombok.Getter;

/**
 * 订单轨迹操作人类型。
 */
@Getter
public enum OrderOperatorType implements CodeEnum {
    CUSTOMER("CUSTOMER", "采购客户"),
    SALES("SALES", "销售"),
    OPERATIONS("OPERATIONS", "运营"),
    WAREHOUSE("WAREHOUSE", "仓库人员"),
    SYSTEM_TASK("SYSTEM_TASK", "系统任务"),
    SYSTEM_SERVICE("SYSTEM_SERVICE", "系统服务");

    private final String code;
    private final String desc;

    OrderOperatorType(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static OrderOperatorType fromCode(String code) {
        return CodeEnum.fromCode(OrderOperatorType.class, code);
    }
}
