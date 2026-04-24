package com.supplychain.service.api.customer.command;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;

/**
 * 更新客户地址命令。
 */
@Data
public class CustomerAddressUpdateCommand implements Serializable {

    /**
     * 收货人姓名。
     */
    @NotBlank(message = "收货人姓名不能为空")
    private String receiverName;

    /**
     * 收货人电话。
     */
    @NotBlank(message = "收货人电话不能为空")
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
    @NotBlank(message = "详细地址不能为空")
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
