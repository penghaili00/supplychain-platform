package com.supplychain.service.provider.order.service;

import com.supplychain.service.provider.order.entity.OrderItem;
import com.supplychain.service.provider.order.mapper.OrderItemMapper;
import com.supplychain.service.provider.support.BaseCrudService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderItemService extends BaseCrudService<OrderItem> {

    public OrderItemService(OrderItemMapper orderItemMapper) {
        super(orderItemMapper, "订单明细");
    }

    public List<OrderItem> listByOrderId(Long orderId) {
        validateId(orderId, "订单ID");
        return listByColumn("order_id", orderId);
    }
}
