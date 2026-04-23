package com.supplychain.service.provider.product.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.supplychain.common.core.enums.common.EnableStatus;
import com.supplychain.common.mybatis.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 商品分类实体
 */
@Data
@TableName("sc_product_category")
@EqualsAndHashCode(callSuper = true)
public class ProductCategory extends BaseEntity {

    /**
     * 父分类ID
     */
    private Long parentId;

    /**
     * 分类名称
     */
    private String categoryName;

    /**
     * 分类编码
     */
    private String categoryCode;

    /**
     * 分类层级
     */
    private Integer level;

    /**
     * 排序值
     */
    private Integer sort;

    /**
     * 分类状态
     */
    private EnableStatus status;

    /**
     * 备注
     */
    private String remark;
}
