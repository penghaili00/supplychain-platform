package com.supplychain.service.provider;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDubbo
@EnableDiscoveryClient
@MapperScan("com.supplychain.service.provider")
@SpringBootApplication(scanBasePackages = "com.supplychain")
public class SupplyChainServiceProviderApplication {

    public static void main(String[] args) {
        SpringApplication.run(SupplyChainServiceProviderApplication.class, args);
    }
}
