package com.supplychain.service.provider.product.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.supplychain.common.core.enums.common.EnableStatus;
import com.supplychain.common.mybatis.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 商品SKU实体
 */
@Data
@TableName("sc_product_sku")
@EqualsAndHashCode(callSuper = true)
public class ProductSku extends BaseEntity {

    /**
     * SKU编码
     */
    private String skuCode;

    /**
     * SPU ID
     */
    private Long spuId;

    /**
     * SKU名称
     */
    private String skuName;

    /**
     * 规格JSON
     */
    private String specJson;

    /**
     * 条码
     */
    private String barCode;

    /**
     * 销售单位
     */
    private String saleUnit;

    /**
     * 包装规格数量
     */
    private BigDecimal packageSize;

    /**
     * 最小起订量
     */
    private BigDecimal minOrderQty;

    /**
     * SKU状态
     */
    private EnableStatus status;

    /**
     * 备注
     */
    private String remark;
}
