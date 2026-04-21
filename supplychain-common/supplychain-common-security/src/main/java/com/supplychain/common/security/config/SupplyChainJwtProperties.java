package com.supplychain.common.security.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@Data
@ConfigurationProperties(prefix = "supplychain.security.jwt")
public class SupplyChainJwtProperties {

    private String issuer = "supplychain";

    private String secret = "SupplyChain-Default-Jwt-Secret-Please-Change-Now-2026";

    private Duration accessExpire = Duration.ofMinutes(30);

    private Duration refreshExpire = Duration.ofDays(7);

    private boolean singleLogin = true;
}
