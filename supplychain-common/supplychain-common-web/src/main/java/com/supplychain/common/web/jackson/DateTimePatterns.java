package com.supplychain.common.web.jackson;

import java.time.format.DateTimeFormatter;

/**
 * Centralized date/time formatters shared by Jackson and MVC converters.
 */
public final class DateTimePatterns {

    public static final String DATE_PATTERN = "yyyy-MM-dd";
    public static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_TIME_MILLIS_PATTERN = "yyyy-MM-dd HH:mm:ss.SSS";

    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_PATTERN);
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);
    public static final DateTimeFormatter DATE_TIME_MILLIS_FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME_MILLIS_PATTERN);

    private DateTimePatterns() {
    }
}
