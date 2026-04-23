package com.supplychain.service.provider.fulfillment.service;

import com.supplychain.service.provider.fulfillment.entity.OutboundOrder;
import com.supplychain.service.provider.fulfillment.mapper.OutboundOrderMapper;
import com.supplychain.service.provider.support.BaseCrudService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OutboundOrderService extends BaseCrudService<OutboundOrder> {

    public OutboundOrderService(OutboundOrderMapper outboundOrderMapper) {
        super(outboundOrderMapper, "出库单");
    }

    public OutboundOrder getByOutboundNo(String outboundNo) {
        validateText(outboundNo, "出库单号");
        return getOneByColumn("outbound_no", outboundNo);
    }

    public List<OutboundOrder> listByOrderId(Long orderId) {
        validateId(orderId, "订单ID");
        return listByColumn("order_id", orderId);
    }

    public List<OutboundOrder> listByWarehouseId(Long warehouseId) {
        validateId(warehouseId, "仓库ID");
        return listByColumn("warehouse_id", warehouseId);
    }
}
