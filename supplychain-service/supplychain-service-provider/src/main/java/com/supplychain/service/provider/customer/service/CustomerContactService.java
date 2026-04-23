package com.supplychain.service.provider.customer.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.supplychain.service.provider.customer.entity.CustomerContact;
import com.supplychain.service.provider.customer.mapper.CustomerContactMapper;
import com.supplychain.service.provider.support.BaseCrudService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerContactService extends BaseCrudService<CustomerContact> {

    public CustomerContactService(CustomerContactMapper customerContactMapper) {
        super(customerContactMapper, "客户联系人");
    }

    public List<CustomerContact> listByCustomerId(Long customerId) {
        validateId(customerId, "客户ID");
        return listByColumn("customer_id", customerId);
    }

    public CustomerContact getDefaultContact(Long customerId) {
        validateId(customerId, "客户ID");
        QueryWrapper<CustomerContact> queryWrapper = activeQuery();
        queryWrapper
                .eq("customer_id", customerId)
                .eq("is_default", 1)
                .last("limit 1");
        return selectOne(queryWrapper);
    }

    public List<CustomerContact> listByCustomerIds(List<Long> customerIds) {
        if (customerIds == null || customerIds.isEmpty()) {
            return List.of();
        }
        QueryWrapper<CustomerContact> queryWrapper = activeQuery();
        queryWrapper.in("customer_id", customerIds)
                .orderByAsc("id");
        return selectList(queryWrapper);
    }
}
