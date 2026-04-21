package com.supplychain.service.api.dubbo;

import com.supplychain.service.api.dto.EsClusterInfoView;
import com.supplychain.service.api.dto.EsProductDocumentView;

import java.util.List;

public interface EsDemoDubboService {

    EsClusterInfoView getClusterInfo();

    void initializeDemoProducts();

    List<EsProductDocumentView> listDemoProducts();
}
