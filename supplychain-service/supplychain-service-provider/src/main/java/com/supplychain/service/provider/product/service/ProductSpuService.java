package com.supplychain.service.provider.product.service;

import com.supplychain.service.provider.product.entity.ProductSpu;
import com.supplychain.service.provider.product.mapper.ProductSpuMapper;
import com.supplychain.service.provider.support.BaseCrudService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductSpuService extends BaseCrudService<ProductSpu> {

    public ProductSpuService(ProductSpuMapper productSpuMapper) {
        super(productSpuMapper, "商品SPU");
    }

    public List<ProductSpu> listByCategoryId(Long categoryId) {
        validateId(categoryId, "分类ID");
        return listByColumn("category_id", categoryId);
    }

    public ProductSpu getBySpuCode(String spuCode) {
        validateText(spuCode, "SPU编码");
        return getOneByColumn("spu_code", spuCode);
    }
}
