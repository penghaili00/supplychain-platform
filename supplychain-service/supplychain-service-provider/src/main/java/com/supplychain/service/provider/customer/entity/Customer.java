package com.supplychain.service.provider.customer.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.supplychain.common.mybatis.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 客户主体实体
 */
@Data
@TableName("sc_customer")
@EqualsAndHashCode(callSuper = true)
public class Customer extends BaseEntity {

    /**
     * 客户编码
     */
    private String customerCode;

    /**
     * 客户名称
     */
    private String customerName;

    /**
     * 客户类型
     */
    private String customerType;

    /**
     * 客户等级
     */
    private String customerLevel;

    /**
     * 销售负责人 ID
     */
    private Long salesOwnerId;

    /**
     * 客户状态
     */
    private String status;

    /**
     * 是否启用授信
     */
    private Integer creditEnabled;

    /**
     * 授信额度
     */
    private BigDecimal creditLimit;

    /**
     * 结算方式
     */
    private String settlementType;

    /**
     * 备注
     */
    private String remark;
}
