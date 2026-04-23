package com.supplychain.service.provider.customer.service;

import com.supplychain.service.provider.customer.entity.Customer;
import com.supplychain.service.provider.customer.mapper.CustomerMapper;
import com.supplychain.service.provider.support.BaseCrudService;
import org.springframework.stereotype.Service;

@Service
public class CustomerService extends BaseCrudService<Customer> {

    private final CustomerMapper customerMapper;

    public CustomerService(CustomerMapper customerMapper) {
        super(customerMapper, "客户");
        this.customerMapper = customerMapper;
    }

    public Customer getByCustomerCode(String customerCode) {
        validateText(customerCode, "客户编码");
        return customerMapper.selectByCustomerCode(customerCode);
    }

    public Customer getByAppUserId(Long appUserId) {
        validateId(appUserId, "App用户ID");
        return customerMapper.selectByAppUserId(appUserId);
    }
}
