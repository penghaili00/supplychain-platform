package com.supplychain.service.provider.search.dubbo;

import com.supplychain.service.api.dubbo.EsDemoDubboService;
import com.supplychain.service.api.dto.EsClusterInfoView;
import com.supplychain.service.api.dto.EsProductDocumentView;
import com.supplychain.service.provider.search.service.ElasticsearchDemoService;
import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboService;

import java.util.List;

@DubboService
@RequiredArgsConstructor
public class EsDemoDubboServiceImpl implements EsDemoDubboService {

    private final ElasticsearchDemoService elasticsearchDemoService;

    @Override
    public EsClusterInfoView getClusterInfo() {
        return elasticsearchDemoService.getClusterInfo();
    }

    @Override
    public void initializeDemoProducts() {
        elasticsearchDemoService.initializeDemoProducts();
    }

    @Override
    public List<EsProductDocumentView> listDemoProducts() {
        return elasticsearchDemoService.listDemoProducts();
    }
}
