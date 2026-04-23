package com.supplychain.service.provider.inventory.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.supplychain.service.provider.inventory.entity.Inventory;
import com.supplychain.service.provider.inventory.mapper.InventoryMapper;
import com.supplychain.service.provider.support.BaseCrudService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InventoryService extends BaseCrudService<Inventory> {

    public InventoryService(InventoryMapper inventoryMapper) {
        super(inventoryMapper, "库存");
    }

    public List<Inventory> listByWarehouseId(Long warehouseId) {
        validateId(warehouseId, "仓库ID");
        return listByColumn("warehouse_id", warehouseId);
    }

    public List<Inventory> listBySkuId(Long skuId) {
        validateId(skuId, "SKU ID");
        return listByColumn("sku_id", skuId);
    }

    public Inventory getByWarehouseAndSkuId(Long warehouseId, Long skuId) {
        validateId(warehouseId, "仓库ID");
        validateId(skuId, "SKU ID");
        QueryWrapper<Inventory> queryWrapper = activeQuery();
        queryWrapper.eq("warehouse_id", warehouseId)
                .eq("sku_id", skuId)
                .last("limit 1");
        return selectOne(queryWrapper);
    }
}
