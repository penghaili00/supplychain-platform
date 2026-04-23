package com.supplychain.service.provider.customer.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.supplychain.service.provider.customer.entity.CustomerAddress;
import com.supplychain.service.provider.customer.mapper.CustomerAddressMapper;
import com.supplychain.service.provider.support.BaseCrudService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerAddressService extends BaseCrudService<CustomerAddress> {

    public CustomerAddressService(CustomerAddressMapper customerAddressMapper) {
        super(customerAddressMapper, "客户地址");
    }

    public List<CustomerAddress> listByCustomerId(Long customerId) {
        validateId(customerId, "客户ID");
        return listByColumn("customer_id", customerId);
    }

    public CustomerAddress getDefaultAddress(Long customerId) {
        validateId(customerId, "客户ID");
        QueryWrapper<CustomerAddress> queryWrapper = activeQuery();
        queryWrapper.eq("customer_id", customerId)
                .eq("is_default", 1)
                .last("limit 1");
        return selectOne(queryWrapper);
    }
}
