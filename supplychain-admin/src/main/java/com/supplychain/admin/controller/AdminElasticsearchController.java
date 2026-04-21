package com.supplychain.admin.controller;

import com.supplychain.common.core.annotation.OperationLog;
import com.supplychain.common.core.domain.R;
import com.supplychain.common.core.domain.SessionUser;
import com.supplychain.common.core.enums.OperationType;
import com.supplychain.common.core.exception.UnauthorizedException;
import com.supplychain.common.security.context.UserContextHolder;
import com.supplychain.service.api.dubbo.EsDemoDubboService;
import com.supplychain.service.api.dto.EsClusterInfoView;
import com.supplychain.service.api.dto.EsProductDocumentView;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class AdminElasticsearchController {

    @DubboReference(check = false)
    private EsDemoDubboService esDemoDubboService;

    @GetMapping("/admin/es/info")
    @OperationLog(title = "查询 Elasticsearch 集群信息", businessType = OperationType.QUERY)
    public R<EsClusterInfoView> info() {
        requireLogin();
        return R.ok(esDemoDubboService.getClusterInfo());
    }

    @PostMapping("/admin/es/demo/init")
    @OperationLog(title = "初始化 Elasticsearch 示例索引", businessType = OperationType.CREATE)
    public R<Void> initializeDemoIndex() {
        requireLogin();
        esDemoDubboService.initializeDemoProducts();
        return R.ok("初始化成功", null);
    }

    @GetMapping("/admin/es/demo/products")
    @OperationLog(title = "查询 Elasticsearch 示例数据", businessType = OperationType.QUERY)
    public R<List<EsProductDocumentView>> listDemoProducts() {
        requireLogin();
        return R.ok(esDemoDubboService.listDemoProducts());
    }

    private void requireLogin() {
        SessionUser sessionUser = UserContextHolder.getUser();
        if (sessionUser == null) {
            throw new UnauthorizedException("未登录");
        }
    }
}
