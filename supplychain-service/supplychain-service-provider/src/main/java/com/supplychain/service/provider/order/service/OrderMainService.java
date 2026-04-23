package com.supplychain.service.provider.order.service;

import com.supplychain.service.provider.order.entity.OrderMain;
import com.supplychain.service.provider.order.mapper.OrderMainMapper;
import com.supplychain.service.provider.support.BaseCrudService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderMainService extends BaseCrudService<OrderMain> {

    public OrderMainService(OrderMainMapper orderMainMapper) {
        super(orderMainMapper, "订单");
    }

    public OrderMain getByOrderNo(String orderNo) {
        validateText(orderNo, "订单号");
        return getOneByColumn("order_no", orderNo);
    }

    public List<OrderMain> listByCustomerId(Long customerId) {
        validateId(customerId, "客户ID");
        return listByColumn("customer_id", customerId);
    }
}
