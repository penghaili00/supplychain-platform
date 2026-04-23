package com.supplychain.service.api.customer.command;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;

/**
 * 更新客户地址命令。
 */
@Data
public class CustomerAddressUpdateCommand implements Serializable {

    @NotBlank(message = "收货人姓名不能为空")
    private String receiverName;

    @NotBlank(message = "收货人电话不能为空")
    private String receiverPhone;

    private String provinceCode;

    private String cityCode;

    private String districtCode;

    @NotBlank(message = "详细地址不能为空")
    private String detailAddress;

    private Integer isDefault;

    private String status;

    private String remark;
}
