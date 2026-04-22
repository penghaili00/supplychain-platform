package com.supplychain.service.provider.customer.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.supplychain.common.core.enums.common.EnableStatus;
import com.supplychain.common.mybatis.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 客户收货地址实体
 */
@Data
@TableName("sc_customer_address")
@EqualsAndHashCode(callSuper = true)
public class CustomerAddress extends BaseEntity {

    /**
     * 客户ID
     */
    private Long customerId;

    /**
     * 收货人姓名
     */
    private String receiverName;

    /**
     * 收货人手机
     */
    private String receiverPhone;

    /**
     * 省份编码
     */
    private String provinceCode;

    /**
     * 城市编码
     */
    private String cityCode;

    /**
     * 区县编码
     */
    private String districtCode;

    /**
     * 详细地址
     */
    private String detailAddress;

    /**
     * 是否默认地址
     */
    private Integer isDefault;

    /**
     * 地址状态
     */
    private EnableStatus status;

    /**
     * 备注
     */
    private String remark;
}
