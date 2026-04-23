package com.supplychain.service.provider.fulfillment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.supplychain.service.provider.fulfillment.entity.OutboundItem;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OutboundItemMapper extends BaseMapper<OutboundItem> {
}
