package com.supplychain.service.provider.search.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;

@Configuration
@EnableConfigurationProperties(SupplyChainElasticsearchProperties.class)
public class SupplyChainElasticsearchConfiguration {

    @Bean
    public RestClient elasticsearchRestClient(SupplyChainElasticsearchProperties properties) {
        RestClient.Builder builder = RestClient.builder()
                .baseUrl(properties.getBaseUrl())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json");
        if (StringUtils.hasText(properties.getUsername())) {
            builder.defaultHeaders(headers -> headers.setBasicAuth(
                    properties.getUsername(),
                    properties.getPassword() == null ? "" : properties.getPassword()
            ));
        }
        return builder.build();
    }
}
