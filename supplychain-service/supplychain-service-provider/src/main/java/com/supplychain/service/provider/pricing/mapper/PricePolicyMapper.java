package com.supplychain.service.provider.pricing.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.supplychain.service.provider.pricing.entity.PricePolicy;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PricePolicyMapper extends BaseMapper<PricePolicy> {
}
