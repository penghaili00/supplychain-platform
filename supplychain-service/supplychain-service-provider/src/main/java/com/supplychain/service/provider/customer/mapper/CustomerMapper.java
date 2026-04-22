package com.supplychain.service.provider.customer.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.supplychain.service.provider.customer.entity.Customer;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface CustomerMapper extends BaseMapper<Customer> {

    @Select("""
            select *
            from sc_customer
            where customer_code = #{customerCode}
              and deleted = 0
            limit 1
            """)
    Customer selectByCustomerCode(@Param("customerCode") String customerCode);

    @Select("""
            select c.*
            from sc_customer c
            inner join app_user u on u.customer_id = c.id
            where u.id = #{appUserId}
              and u.deleted = 0
              and c.deleted = 0
            limit 1
            """)
    Customer selectByAppUserId(@Param("appUserId") Long appUserId);
}
