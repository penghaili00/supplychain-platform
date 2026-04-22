package com.supplychain.service.provider.customer.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.supplychain.common.core.enums.common.EnableStatus;
import com.supplychain.common.mybatis.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 客户联系人实体
 */
@Data
@TableName("sc_customer_contact")
@EqualsAndHashCode(callSuper = true)
public class CustomerContact extends BaseEntity {

    /**
     * 客户ID
     */
    private Long customerId;

    /**
     * 联系人姓名
     */
    private String contactName;

    /**
     * 手机号
     */
    private String mobile;

    /**
     * 座机号
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 是否默认联系人
     */
    private Integer isDefault;

    /**
     * 状态
     */
    private EnableStatus status;

    /**
     * 备注
     */
    private String remark;
}
