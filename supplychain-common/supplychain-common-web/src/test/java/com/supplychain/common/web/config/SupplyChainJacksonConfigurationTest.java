package com.supplychain.common.web.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.supplychain.common.web.jackson.LocalDateTimeMillisSerializer;
import org.junit.jupiter.api.Test;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.format.support.DefaultFormattingConversionService;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class SupplyChainJacksonConfigurationTest {

    @Test
    void shouldSerializeAndDeserializeLocalDateAndLocalDateTime() throws Exception {
        Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
        new SupplyChainJacksonConfiguration().jackson2ObjectMapperBuilderCustomizer().customize(builder);
        ObjectMapper objectMapper = builder.build();

        DemoPayload payload = new DemoPayload();
        payload.setDate(LocalDate.of(2026, 4, 22));
        payload.setDateTime(LocalDateTime.of(2026, 4, 22, 9, 30, 45));
        payload.setDateTimeWithMillis(LocalDateTime.of(2026, 4, 22, 9, 30, 45, 123_000_000));

        String json = objectMapper.writeValueAsString(payload);

        assertThat(json).contains("\"date\":\"2026-04-22\"");
        assertThat(json).contains("\"dateTime\":\"2026-04-22 09:30:45\"");
        assertThat(json).contains("\"dateTimeWithMillis\":\"2026-04-22 09:30:45.123\"");

        DemoPayload parsed = objectMapper.readValue("""
                {
                  "date":"2026-04-22",
                  "dateTime":"2026-04-22 09:30:45.123"
                }
                """, DemoPayload.class);

        assertThat(parsed.getDate()).isEqualTo(LocalDate.of(2026, 4, 22));
        assertThat(parsed.getDateTime()).isEqualTo(LocalDateTime.of(2026, 4, 22, 9, 30, 45, 123_000_000));
    }

    @Test
    void shouldSupportStringConvertersForMvcBinding() {
        DefaultFormattingConversionService conversionService = new DefaultFormattingConversionService();
        new SupplyChainDateTimeWebMvcConfiguration().addFormatters(conversionService);

        assertThat(conversionService.convert("2026-04-22", LocalDate.class))
                .isEqualTo(LocalDate.of(2026, 4, 22));
        assertThat(conversionService.convert("2026-04-22 09:30:45", LocalDateTime.class))
                .isEqualTo(LocalDateTime.of(2026, 4, 22, 9, 30, 45));
        assertThat(conversionService.convert("2026-04-22 09:30:45.123", LocalDateTime.class))
                .isEqualTo(LocalDateTime.of(2026, 4, 22, 9, 30, 45, 123_000_000));
        assertThat(conversionService.convert("2026-04-22", LocalDateTime.class))
                .isEqualTo(LocalDateTime.of(2026, 4, 22, 0, 0));
    }

    private static class DemoPayload {

        private LocalDate date;

        private LocalDateTime dateTime;

        @JsonSerialize(using = LocalDateTimeMillisSerializer.class)
        private LocalDateTime dateTimeWithMillis;

        public LocalDate getDate() {
            return date;
        }

        public void setDate(LocalDate date) {
            this.date = date;
        }

        public LocalDateTime getDateTime() {
            return dateTime;
        }

        public void setDateTime(LocalDateTime dateTime) {
            this.dateTime = dateTime;
        }

        public LocalDateTime getDateTimeWithMillis() {
            return dateTimeWithMillis;
        }

        public void setDateTimeWithMillis(LocalDateTime dateTimeWithMillis) {
            this.dateTimeWithMillis = dateTimeWithMillis;
        }
    }
}
