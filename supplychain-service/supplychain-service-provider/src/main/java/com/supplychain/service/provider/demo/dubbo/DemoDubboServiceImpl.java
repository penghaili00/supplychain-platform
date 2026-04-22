package com.supplychain.service.provider.demo.dubbo;

import com.supplychain.service.api.demo.dubbo.DemoDubboService;
import org.apache.dubbo.config.annotation.DubboService;

import java.time.LocalDateTime;

@DubboService
public class DemoDubboServiceImpl implements DemoDubboService {

    @Override
    public String ping(String source) {
        return "SupplyChain provider pong from " + source + " at " + LocalDateTime.now();
    }
}
