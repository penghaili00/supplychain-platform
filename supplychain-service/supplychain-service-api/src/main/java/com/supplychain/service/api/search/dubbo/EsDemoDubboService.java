package com.supplychain.service.api.search.dubbo;

import com.supplychain.service.api.search.view.EsClusterInfoView;
import com.supplychain.service.api.search.view.EsProductDocumentView;

import java.util.List;

public interface EsDemoDubboService {

    EsClusterInfoView getClusterInfo();

    void initializeDemoProducts();

    List<EsProductDocumentView> listDemoProducts();
}
