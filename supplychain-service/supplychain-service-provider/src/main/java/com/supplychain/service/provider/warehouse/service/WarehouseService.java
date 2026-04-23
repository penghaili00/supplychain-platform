package com.supplychain.service.provider.warehouse.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.supplychain.service.provider.support.BaseCrudService;
import com.supplychain.service.provider.warehouse.entity.Warehouse;
import com.supplychain.service.provider.warehouse.mapper.WarehouseMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WarehouseService extends BaseCrudService<Warehouse> {

    public WarehouseService(WarehouseMapper warehouseMapper) {
        super(warehouseMapper, "仓库");
    }

    public Warehouse getByWarehouseCode(String warehouseCode) {
        validateText(warehouseCode, "仓库编码");
        return getOneByColumn("warehouse_code", warehouseCode);
    }

    public List<Warehouse> listByRegionCode(String regionCode) {
        validateText(regionCode, "区域编码");
        return listByColumn("region_code", regionCode);
    }

    public List<Warehouse> listEnabled() {
        QueryWrapper<Warehouse> queryWrapper = activeQuery();
        queryWrapper.eq("status", "ENABLED")
                .orderByAsc("priority")
                .orderByAsc("id");
        return selectList(queryWrapper);
    }
}
