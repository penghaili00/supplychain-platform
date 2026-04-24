package com.supplychain.service.api.customer.view;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 客户地址视图。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerAddressView implements Serializable {

    /**
     * 地址 ID。
     */
    private Long addressId;

    /**
     * 客户 ID。
     */
    private Long customerId;

    /**
     * 收货人姓名。
     */
    private String receiverName;

    /**
     * 收货人电话。
     */
    private String receiverPhone;

    /**
     * 省编码。
     */
    private String provinceCode;

    /**
     * 市编码。
     */
    private String cityCode;

    /**
     * 区编码。
     */
    private String districtCode;

    /**
     * 详细地址。
     */
    private String detailAddress;

    /**
     * 是否默认地址。
     */
    private Integer isDefault;

    /**
     * 地址状态。
     */
    private String status;

    /**
     * 备注。
     */
    private String remark;
}
