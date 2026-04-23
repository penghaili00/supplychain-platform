package com.supplychain.service.provider.product.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.supplychain.common.core.enums.common.EnableStatus;
import com.supplychain.common.core.enums.product.ProductSaleStatus;
import com.supplychain.common.mybatis.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 商品SPU实体
 */
@Data
@TableName("sc_product_spu")
@EqualsAndHashCode(callSuper = true)
public class ProductSpu extends BaseEntity {

    /**
     * SPU编码
     */
    private String spuCode;

    /**
     * SPU名称
     */
    private String spuName;

    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 品牌名称
     */
    private String brandName;

    /**
     * 基础单位名称
     */
    private String unitName;

    /**
     * 销售状态
     */
    private ProductSaleStatus saleStatus;

    /**
     * 数据状态
     */
    private EnableStatus status;

    /**
     * 备注
     */
    private String remark;
}
