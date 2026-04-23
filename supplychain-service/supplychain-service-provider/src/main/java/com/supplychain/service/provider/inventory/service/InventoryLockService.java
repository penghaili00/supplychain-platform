package com.supplychain.service.provider.inventory.service;

import com.supplychain.service.provider.inventory.entity.InventoryLock;
import com.supplychain.service.provider.inventory.mapper.InventoryLockMapper;
import com.supplychain.service.provider.support.BaseCrudService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InventoryLockService extends BaseCrudService<InventoryLock> {

    public InventoryLockService(InventoryLockMapper inventoryLockMapper) {
        super(inventoryLockMapper, "库存锁定记录");
    }

    public List<InventoryLock> listByOrderId(Long orderId) {
        validateId(orderId, "订单ID");
        return listByColumn("order_id", orderId);
    }

    public List<InventoryLock> listByOrderItemId(Long orderItemId) {
        validateId(orderItemId, "订单明细ID");
        return listByColumn("order_item_id", orderItemId);
    }
}
