package com.supplychain.service.provider.pricing.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.supplychain.common.core.enums.common.EnableStatus;
import com.supplychain.common.core.enums.pricing.PriceType;
import com.supplychain.common.mybatis.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 价格策略实体
 */
@Data
@TableName("sc_price_policy")
@EqualsAndHashCode(callSuper = true)
public class PricePolicy extends BaseEntity {

    /**
     * 客户ID
     */
    private Long customerId;

    /**
     * SKU ID
     */
    private Long skuId;

    /**
     * 价格类型
     */
    private PriceType priceType;

    /**
     * 销售单价
     */
    private BigDecimal salePrice;

    /**
     * 最小起订量
     */
    private BigDecimal minOrderQty;

    /**
     * 生效开始时间
     */
    private LocalDateTime effectiveStartTime;

    /**
     * 生效结束时间
     */
    private LocalDateTime effectiveEndTime;

    /**
     * 价格状态
     */
    private EnableStatus status;

    /**
     * 备注
     */
    private String remark;
}
