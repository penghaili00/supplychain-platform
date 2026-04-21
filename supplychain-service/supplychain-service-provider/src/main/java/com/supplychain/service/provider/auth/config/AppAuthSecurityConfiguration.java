package com.supplychain.service.provider.auth.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(AppAuthSecurityProperties.class)
public class AppAuthSecurityConfiguration {
}
