package com.supplychain.service.provider.fulfillment.service;

import com.supplychain.service.provider.fulfillment.entity.ShipmentOrder;
import com.supplychain.service.provider.fulfillment.mapper.ShipmentOrderMapper;
import com.supplychain.service.provider.support.BaseCrudService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShipmentOrderService extends BaseCrudService<ShipmentOrder> {

    public ShipmentOrderService(ShipmentOrderMapper shipmentOrderMapper) {
        super(shipmentOrderMapper, "发货单");
    }

    public ShipmentOrder getByShipmentNo(String shipmentNo) {
        validateText(shipmentNo, "发货单号");
        return getOneByColumn("shipment_no", shipmentNo);
    }

    public List<ShipmentOrder> listByOrderId(Long orderId) {
        validateId(orderId, "订单ID");
        return listByColumn("order_id", orderId);
    }

    public List<ShipmentOrder> listByOutboundId(Long outboundId) {
        validateId(outboundId, "出库单ID");
        return listByColumn("outbound_id", outboundId);
    }
}
