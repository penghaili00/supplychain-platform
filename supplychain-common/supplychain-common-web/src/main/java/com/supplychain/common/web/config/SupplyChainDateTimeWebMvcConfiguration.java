package com.supplychain.common.web.config;

import com.supplychain.common.web.jackson.DateTimePatterns;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Configuration
public class SupplyChainDateTimeWebMvcConfiguration implements WebMvcConfigurer {

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(String.class, LocalDate.class, source -> {
            String value = normalize(source);
            if (value == null) {
                return null;
            }
            if (value.length() == DateTimePatterns.DATE_PATTERN.length()) {
                return LocalDate.parse(value, DateTimePatterns.DATE_FORMATTER);
            }
            return LocalDateTime.parse(value, resolveDateTimeFormatter(value)).toLocalDate();
        });

        registry.addConverter(String.class, LocalDateTime.class, source -> {
            String value = normalize(source);
            if (value == null) {
                return null;
            }
            if (value.length() == DateTimePatterns.DATE_PATTERN.length()) {
                return LocalDate.parse(value, DateTimePatterns.DATE_FORMATTER).atStartOfDay();
            }
            return LocalDateTime.parse(value, resolveDateTimeFormatter(value));
        });
    }

    private static String normalize(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim().replace('T', ' ');
    }

    private static java.time.format.DateTimeFormatter resolveDateTimeFormatter(String value) {
        return value.contains(".") ? DateTimePatterns.DATE_TIME_MILLIS_FORMATTER : DateTimePatterns.DATE_TIME_FORMATTER;
    }
}
