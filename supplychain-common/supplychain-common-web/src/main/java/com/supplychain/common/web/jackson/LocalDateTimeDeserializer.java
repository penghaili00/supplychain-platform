package com.supplychain.common.web.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class LocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {

    @Override
    public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String text = p.getValueAsString();
        if (text == null || text.isBlank()) {
            return null;
        }

        String value = text.trim();
        try {
            if (value.length() == DateTimePatterns.DATE_PATTERN.length()) {
                return LocalDate.parse(value, DateTimePatterns.DATE_FORMATTER).atStartOfDay();
            }
            String normalized = value.contains("T") ? value.replace('T', ' ') : value;
            return LocalDateTime.parse(normalized, value.contains(".")
                    ? DateTimePatterns.DATE_TIME_MILLIS_FORMATTER
                    : DateTimePatterns.DATE_TIME_FORMATTER);
        } catch (Exception ex) {
            throw InvalidFormatException.from(p, "Unsupported LocalDateTime format: " + value, value, LocalDateTime.class);
        }
    }
}
