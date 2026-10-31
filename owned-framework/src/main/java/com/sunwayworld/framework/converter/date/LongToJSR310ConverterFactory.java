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

public enum LongToJSR310ConverterFactory implements ConverterFactory<Long, Temporal> {
    INSTANCE;

    public <T extends Temporal> Converter<Long, T> getConverter(Class<T> targetType) {
        return new LongToJSR310Converter<T>(targetType);
    }

    class LongToJSR310Converter<T extends Temporal> implements Converter<Long, T> {
        private final Class<T> jsr310Type;

        public LongToJSR310Converter(Class<T> jsr310Type) {
            this.jsr310Type = jsr310Type;
        }

        public Class<Long> getSourceType() {
            return Long.class;
        }

        public Class<T> getTargetType() {
            return jsr310Type;
        }

        public T convert(Long source) {
            if (source == null) return null;
            Class<T> target = getTargetType();
            java.time.ZonedDateTime zoned = new Date(source.longValue()).toInstant().atZone(ZoneId.systemDefault());
            if (LocalDateTime.class == target) return target.cast(zoned.toLocalDateTime());
            if (LocalDate.class == target) return target.cast(zoned.toLocalDate());
            if (LocalTime.class == target) return target.cast(zoned.toLocalTime());
            throw new ConverterNotFoundException(getSourceType(), target);
        }
    }
}
