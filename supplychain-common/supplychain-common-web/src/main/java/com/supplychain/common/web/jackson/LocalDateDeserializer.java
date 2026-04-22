package com.supplychain.common.web.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class LocalDateDeserializer extends JsonDeserializer<LocalDate> {

    @Override
    public LocalDate deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String text = p.getValueAsString();
        if (text == null || text.isBlank()) {
            return null;
        }

        String value = text.trim();
        try {
            if (value.length() == DateTimePatterns.DATE_PATTERN.length()) {
                return LocalDate.parse(value, DateTimePatterns.DATE_FORMATTER);
            }
            return LocalDateTime.parse(normalizeDateTime(value), resolveDateTimeFormatter(value)).toLocalDate();
        } catch (Exception ex) {
            throw InvalidFormatException.from(p, "Unsupported LocalDate format: " + value, value, LocalDate.class);
        }
    }

    private static String normalizeDateTime(String value) {
        return value.contains("T") ? value.replace('T', ' ') : value;
    }

    private static java.time.format.DateTimeFormatter resolveDateTimeFormatter(String value) {
        return value.contains(".") ? DateTimePatterns.DATE_TIME_MILLIS_FORMATTER : DateTimePatterns.DATE_TIME_FORMATTER;
    }
}
