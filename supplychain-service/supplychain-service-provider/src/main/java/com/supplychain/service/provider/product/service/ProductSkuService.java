package com.supplychain.service.provider.product.service;

import com.supplychain.service.provider.product.entity.ProductSku;
import com.supplychain.service.provider.product.mapper.ProductSkuMapper;
import com.supplychain.service.provider.support.BaseCrudService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductSkuService extends BaseCrudService<ProductSku> {

    public ProductSkuService(ProductSkuMapper productSkuMapper) {
        super(productSkuMapper, "商品SKU");
    }

    public List<ProductSku> listBySpuId(Long spuId) {
        validateId(spuId, "SPU ID");
        return listByColumn("spu_id", spuId);
    }

    public ProductSku getBySkuCode(String skuCode) {
        validateText(skuCode, "SKU编码");
        return getOneByColumn("sku_code", skuCode);
    }
}
