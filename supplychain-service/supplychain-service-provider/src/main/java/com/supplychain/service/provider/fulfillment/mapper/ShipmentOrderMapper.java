package com.supplychain.service.provider.fulfillment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.supplychain.service.provider.fulfillment.entity.ShipmentOrder;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ShipmentOrderMapper extends BaseMapper<ShipmentOrder> {
}
