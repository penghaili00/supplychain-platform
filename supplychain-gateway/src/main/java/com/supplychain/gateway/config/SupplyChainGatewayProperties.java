package com.supplychain.gateway.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@Data
@ConfigurationProperties(prefix = "supplychain.gateway")
public class SupplyChainGatewayProperties {

    private List<String> ignoreUrls = new ArrayList<>();
}
