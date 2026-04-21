package com.supplychain.service.provider.auth.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@RefreshScope
@ConfigurationProperties(prefix = "supplychain.security.app-auth")
public class AppAuthSecurityProperties {

    private String signSecret = "SupplyChain-App-Sign-Secret-Please-Change-Now-2026";

    private Duration allowedTimestampSkew = Duration.ofMinutes(5);

    private Duration nonceTtl = Duration.ofMinutes(5);

    private Password password = new Password();

    private List<LimitRule> accountFailureRules = new ArrayList<>(List.of(
            new LimitRule(Duration.ofMinutes(30), 5, Duration.ofMinutes(10)),
            new LimitRule(Duration.ofHours(2), 15, Duration.ofHours(1)),
            new LimitRule(Duration.ofDays(1), 20, Duration.ofDays(1))
    ));

    private List<LimitRule> ipFailureRules = new ArrayList<>(List.of(
            new LimitRule(Duration.ofDays(1), 30, Duration.ofDays(1))
    ));

    @Setter
    @Getter
    public static class Password {

        private int iterations = 120000;
        private int hashBytes = 32;

    }

    @Setter
    @Getter
    public static class LimitRule {

        private Duration window;
        private int threshold;
        private Duration lockDuration;

        public LimitRule() {
        }

        public LimitRule(Duration window, int threshold, Duration lockDuration) {
            this.window = window;
            this.threshold = threshold;
            this.lockDuration = lockDuration;
        }

    }
}
