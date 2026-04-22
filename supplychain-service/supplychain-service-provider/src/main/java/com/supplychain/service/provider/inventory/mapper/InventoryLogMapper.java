package com.supplychain.service.provider.inventory.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.supplychain.service.provider.inventory.entity.InventoryLog;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface InventoryLogMapper extends BaseMapper<InventoryLog> {
}
