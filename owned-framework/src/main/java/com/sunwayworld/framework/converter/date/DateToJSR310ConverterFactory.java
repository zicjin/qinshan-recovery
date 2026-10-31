package com.sunwayworld.framework.converter.date;

import com.sunwayworld.framework.converter.Converter;
import com.sunwayworld.framework.converter.ConverterFactory;
import com.sunwayworld.framework.converter.exception.ConverterNotFoundException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.Temporal;
import java.util.Date;

public enum DateToJSR310ConverterFactory implements ConverterFactory<Date, Temporal> {
    INSTANCE;

    public <T extends Temporal> Converter<Date, T> getConverter(Class<T> targetType) {
        return new DateToJSR310Converter<T>(targetType);
    }

    class DateToJSR310Converter<T extends Temporal> implements Converter<Date, T> {
        private final Class<T> jsr310Type;

        public DateToJSR310Converter(Class<T> jsr310Type) {
            this.jsr310Type = jsr310Type;
        }

        public Class<Date> getSourceType() {
            return Date.class;
        }

        public Class<T> getTargetType() {
            return jsr310Type;
        }

        public T convert(Date source) {
            if (source == null) return null;
            Class<T> target = getTargetType();
            Date normalized = java.sql.Date.class.equals(source.getClass()) ? new Date(source.getTime()) : source;
            java.time.ZonedDateTime zoned = normalized.toInstant().atZone(ZoneId.systemDefault());
            if (LocalDateTime.class == target) return target.cast(zoned.toLocalDateTime());
            if (LocalDate.class == target) return target.cast(zoned.toLocalDate());
            if (LocalTime.class == target) return target.cast(zoned.toLocalTime());
            throw new ConverterNotFoundException(getSourceType(), target);
        }
    }
}
