package com.supplychain.service.provider.pricing.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.supplychain.service.provider.pricing.entity.PricePolicy;
import com.supplychain.service.provider.pricing.mapper.PricePolicyMapper;
import com.supplychain.service.provider.support.BaseCrudService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PricePolicyService extends BaseCrudService<PricePolicy> {

    public PricePolicyService(PricePolicyMapper pricePolicyMapper) {
        super(pricePolicyMapper, "价格策略");
    }

    public List<PricePolicy> listByCustomerId(Long customerId) {
        validateId(customerId, "客户ID");
        return listByColumn("customer_id", customerId);
    }

    public List<PricePolicy> listBasePolicies() {
        QueryWrapper<PricePolicy> queryWrapper = activeQuery();
        queryWrapper.isNull("customer_id").orderByAsc("id");
        return selectList(queryWrapper);
    }

    public List<PricePolicy> listBySkuId(Long skuId) {
        validateId(skuId, "SKU ID");
        return listByColumn("sku_id", skuId);
    }
}
