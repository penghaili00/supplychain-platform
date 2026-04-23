package com.supplychain.service.provider.inventory.service;

import com.supplychain.service.provider.inventory.entity.InventoryLog;
import com.supplychain.service.provider.inventory.mapper.InventoryLogMapper;
import com.supplychain.service.provider.support.BaseCrudService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InventoryLogService extends BaseCrudService<InventoryLog> {

    public InventoryLogService(InventoryLogMapper inventoryLogMapper) {
        super(inventoryLogMapper, "库存流水");
    }

    public List<InventoryLog> listByWarehouseId(Long warehouseId) {
        validateId(warehouseId, "仓库ID");
        return listByColumn("warehouse_id", warehouseId);
    }

    public List<InventoryLog> listByBizNo(String bizNo) {
        validateText(bizNo, "业务单号");
        return listByColumn("biz_no", bizNo);
    }
}
