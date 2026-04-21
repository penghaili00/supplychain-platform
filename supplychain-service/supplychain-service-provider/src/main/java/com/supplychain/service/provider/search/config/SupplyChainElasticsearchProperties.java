package com.supplychain.service.provider.search.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "supplychain.elasticsearch")
public class SupplyChainElasticsearchProperties {

    private String baseUrl = "http://127.0.0.1:9200";

    private String username = "elastic";

    private String password;
}
