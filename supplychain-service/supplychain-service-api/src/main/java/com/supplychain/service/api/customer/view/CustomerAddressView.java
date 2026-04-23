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

    private Long addressId;

    private Long customerId;

    private String receiverName;

    private String receiverPhone;

    private String provinceCode;

    private String cityCode;

    private String districtCode;

    private String detailAddress;

    private Integer isDefault;

    private String status;

    private String remark;
}
