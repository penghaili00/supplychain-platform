package com.supplychain.gateway;

import com.supplychain.gateway.config.SupplyChainGatewayProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@EnableConfigurationProperties(SupplyChainGatewayProperties.class)
@SpringBootApplication(scanBasePackages = "com.supplychain")
public class SupplyChainGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(SupplyChainGatewayApplication.class, args);
    }
}
