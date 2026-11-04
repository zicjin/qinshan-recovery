/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.converter.date;

import com.sunwayworld.framework.converter.Converter;
import com.sunwayworld.framework.converter.ConverterFactory;
import com.sunwayworld.framework.converter.exception.ConverterNotFoundException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.Temporal;
import java.util.Date;

public enum LongToJSR310ConverterFactory implements ConverterFactory<Long, Temporal>
{
    INSTANCE;


    @Override
    public <T extends Temporal> Converter<Long, T> getConverter(Class<T> targetType) {
        return new LongToJSR310Converter<T>(targetType);
    }

    private class LongToJSR310Converter<T extends Temporal>
    implements Converter<Long, T> {
        private final Class<T> jsr310Type;

        public LongToJSR310Converter(Class<T> jsr310Type) {
            this.jsr310Type = jsr310Type;
        }

        @Override
        public Class<Long> getSourceType() {
            return Long.class;
        }

        @Override
        public Class<T> getTargetType() {
            return this.jsr310Type;
        }

        @Override
        public T convert(Long source) {
            if (source == null) {
                return null;
            }
            Class<T> targetType = this.getTargetType();
            ZonedDateTime zondedDateTime = new Date(source).toInstant().atZone(ZoneId.systemDefault());
            if (targetType == LocalDateTime.class) {
                return (T)zondedDateTime.toLocalDateTime();
            }
            if (targetType == LocalDate.class) {
                return (T)zondedDateTime.toLocalDate();
            }
            if (targetType == LocalTime.class) {
                return (T)zondedDateTime.toLocalTime();
            }
            throw new ConverterNotFoundException(this.getSourceType(), targetType);
        }
    }
}
