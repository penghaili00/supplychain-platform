package com.supplychain.mq;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages = "com.supplychain")
public class SupplyChainMqApplication {

    public static void main(String[] args) {
        SpringApplication.run(SupplyChainMqApplication.class, args);
    }
}
