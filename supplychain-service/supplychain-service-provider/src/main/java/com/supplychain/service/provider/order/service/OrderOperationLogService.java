package com.supplychain.service.provider.order.service;

import com.supplychain.service.provider.order.entity.OrderOperationLog;
import com.supplychain.service.provider.order.mapper.OrderOperationLogMapper;
import com.supplychain.service.provider.support.BaseCrudService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderOperationLogService extends BaseCrudService<OrderOperationLog> {

    public OrderOperationLogService(OrderOperationLogMapper orderOperationLogMapper) {
        super(orderOperationLogMapper, "订单操作日志");
    }

    public List<OrderOperationLog> listByOrderId(Long orderId) {
        validateId(orderId, "订单ID");
        return listByColumn("order_id", orderId);
    }
}
