package com.sunwayworld.framework.converter.string;

import com.sunwayworld.framework.converter.Converter;
import com.sunwayworld.framework.converter.ConverterFactory;
import com.sunwayworld.framework.converter.exception.ConverterNotFoundException;
import com.sunwayworld.owned.internal.converter.DateTimeState;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.Temporal;

public enum StringToJSR310ConverterFactory implements ConverterFactory<String, Temporal> {
    INSTANCE;
    public <T extends Temporal> Converter<String, T> getConverter(Class<T> targetType) { return new StringToJSR310Converter<T>(targetType); }
    class StringToJSR310Converter<T extends Temporal> implements Converter<String, T> {
        private final Class<T> jsr310Type;
        public StringToJSR310Converter(Class<T> jsr310Type) { this.jsr310Type = jsr310Type; }
        public Class<String> getSourceType() { return String.class; }
        public Class<T> getTargetType() { return jsr310Type; }
        @SuppressWarnings("unchecked") public T convert(String source) {
            if (source == null) return null;
            if (LocalDateTime.class.equals(jsr310Type)) return (T) DateTimeState.parseLocalDateTime(source);
            if (LocalDate.class.equals(jsr310Type)) return (T) DateTimeState.parseLocalDate(source);
            if (LocalTime.class.equals(jsr310Type)) return (T) DateTimeState.parseLocalTime(source);
            throw new ConverterNotFoundException(getSourceType(), jsr310Type);
        }
    }
}
