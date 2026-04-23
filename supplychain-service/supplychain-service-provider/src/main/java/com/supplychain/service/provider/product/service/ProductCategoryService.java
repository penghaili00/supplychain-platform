package com.supplychain.service.provider.product.service;

import com.supplychain.service.provider.product.entity.ProductCategory;
import com.supplychain.service.provider.product.mapper.ProductCategoryMapper;
import com.supplychain.service.provider.support.BaseCrudService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductCategoryService extends BaseCrudService<ProductCategory> {

    public ProductCategoryService(ProductCategoryMapper productCategoryMapper) {
        super(productCategoryMapper, "商品分类");
    }

    public List<ProductCategory> listByParentId(Long parentId) {
        if (parentId == null) {
            parentId = 0L;
        }
        return listByColumn("parent_id", parentId);
    }

    public ProductCategory getByCategoryCode(String categoryCode) {
        validateText(categoryCode, "分类编码");
        return getOneByColumn("category_code", categoryCode);
    }
}
