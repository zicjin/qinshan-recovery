/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.converter.string;

import com.sunwayworld.framework.converter.Converter;
import com.sunwayworld.framework.converter.ConverterFactory;
import com.sunwayworld.framework.converter.exception.ConverterNotFoundException;
import com.sunwayworld.framework.utils.DateTimeUtils;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.Temporal;

public enum StringToJSR310ConverterFactory implements ConverterFactory<String, Temporal>
{
    INSTANCE;


    @Override
    public <T extends Temporal> Converter<String, T> getConverter(Class<T> targetType) {
        return new StringToJSR310Converter<T>(targetType);
    }

    private class StringToJSR310Converter<T extends Temporal>
    implements Converter<String, T> {
        private final Class<T> jsr310Type;

        public StringToJSR310Converter(Class<T> jsr310Type) {
            this.jsr310Type = jsr310Type;
        }

        @Override
        public Class<String> getSourceType() {
            return String.class;
        }

        @Override
        public Class<T> getTargetType() {
            return this.jsr310Type;
        }

        @Override
        public T convert(String source) {
            if (source == null) {
                return null;
            }
            Class<T> targetType = this.getTargetType();
            if (LocalDateTime.class.equals(targetType)) {
                return (T)DateTimeUtils.parseLocalDateTime(source);
            }
            if (LocalDate.class.equals(targetType)) {
                return (T)DateTimeUtils.parseLocalDate(source);
            }
            if (LocalTime.class.equals(targetType)) {
                return (T)DateTimeUtils.parseLocalTime(source);
            }
            throw new ConverterNotFoundException(this.getSourceType(), targetType);
        }
    }
}
