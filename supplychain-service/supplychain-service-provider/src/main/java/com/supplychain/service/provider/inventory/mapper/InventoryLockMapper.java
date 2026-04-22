package com.supplychain.service.provider.inventory.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.supplychain.service.provider.inventory.entity.InventoryLock;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface InventoryLockMapper extends BaseMapper<InventoryLock> {
}
