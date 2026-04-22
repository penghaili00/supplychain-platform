package com.supplychain.common.core.enums.order;

import com.supplychain.common.core.enums.CodeEnum;
import lombok.Getter;

/**
 * 订单业务轨迹操作类型。
 */
@Getter
public enum OrderOperationType implements CodeEnum {
    CREATE("CREATE", "创建订单"),
    AUDIT_APPROVE("AUDIT_APPROVE", "审核通过"),
    AUDIT_REJECT("AUDIT_REJECT", "审核驳回"),
    LOCK_SUCCESS("LOCK_SUCCESS", "锁库成功"),
    LOCK_FAILED("LOCK_FAILED", "锁库失败"),
    OUTBOUND_CREATE("OUTBOUND_CREATE", "生成出库单"),
    OUTBOUND_FINISH("OUTBOUND_FINISH", "出库完成"),
    SHIPMENT_REGISTER("SHIPMENT_REGISTER", "登记发货"),
    RECEIPT_CONFIRM("RECEIPT_CONFIRM", "确认收货"),
    AUTO_RECEIPT_CONFIRM("AUTO_RECEIPT_CONFIRM", "自动确认收货"),
    CANCEL("CANCEL", "取消订单"),
    REMARK("REMARK", "订单备注"),
    CLOSE("CLOSE", "关闭订单");

    private final String code;
    private final String desc;

    OrderOperationType(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static OrderOperationType fromCode(String code) {
        return CodeEnum.fromCode(OrderOperationType.class, code);
    }
}
