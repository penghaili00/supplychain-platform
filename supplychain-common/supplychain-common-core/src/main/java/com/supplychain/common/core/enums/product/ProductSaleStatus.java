package com.supplychain.common.core.enums.product;

import com.supplychain.common.core.enums.CodeEnum;
import lombok.Getter;

/**
 * 商品销售状态。
 */
@Getter
public enum ProductSaleStatus implements CodeEnum {
    ON_SHELF("ON_SHELF", "上架"),
    OFF_SHELF("OFF_SHELF", "下架");

    private final String code;
    private final String desc;

    ProductSaleStatus(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static ProductSaleStatus fromCode(String code) {
        return CodeEnum.fromCode(ProductSaleStatus.class, code);
    }
}
