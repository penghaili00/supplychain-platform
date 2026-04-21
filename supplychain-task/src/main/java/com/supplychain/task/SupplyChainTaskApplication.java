package com.supplychain.task;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableDubbo
@EnableScheduling
@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages = "com.supplychain")
public class SupplyChainTaskApplication {

    public static void main(String[] args) {
        SpringApplication.run(SupplyChainTaskApplication.class, args);
    }
}
