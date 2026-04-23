package com.supplychain.service.provider.fulfillment.service;

import com.supplychain.service.provider.fulfillment.entity.OutboundItem;
import com.supplychain.service.provider.fulfillment.mapper.OutboundItemMapper;
import com.supplychain.service.provider.support.BaseCrudService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OutboundItemService extends BaseCrudService<OutboundItem> {

    public OutboundItemService(OutboundItemMapper outboundItemMapper) {
        super(outboundItemMapper, "出库单明细");
    }

    public List<OutboundItem> listByOutboundId(Long outboundId) {
        validateId(outboundId, "出库单ID");
        return listByColumn("outbound_id", outboundId);
    }

    public List<OutboundItem> listByOrderItemId(Long orderItemId) {
        validateId(orderItemId, "订单明细ID");
        return listByColumn("order_item_id", orderItemId);
    }
}
