package com.sunwayworld.framework.converter.date;

import com.sunwayworld.framework.converter.Converter;
import com.sunwayworld.framework.converter.exception.ConverterNotFoundException;
import com.sunwayworld.owned.internal.converter.DateTimeState;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.Temporal;

public interface JSR310ToStringGenericConverter<S extends Temporal> extends Converter<S, String> {
    @SuppressWarnings("unchecked") default Class<S> getSourceType() { return (Class<S>) ((java.lang.reflect.ParameterizedType) getClass().getGenericInterfaces()[0]).getActualTypeArguments()[0]; }
    default Class<String> getTargetType() { return String.class; }
    default String convert(S source) {
        if (source == null) return null;
        Class<S> type = getSourceType();
        if (LocalDateTime.class.equals(type)) return DateTimeState.formatLocalDateTime((LocalDateTime) source);
        if (LocalDate.class.equals(type)) return DateTimeState.formatLocalDate((LocalDate) source);
        if (LocalTime.class.equals(type)) return DateTimeState.formatLocalTime((LocalTime) source);
        throw new ConverterNotFoundException(type, getTargetType());
    }
    enum LocalDateTimeToStringConverter implements JSR310ToStringGenericConverter<LocalDateTime> { INSTANCE }
    enum LocalDateToStringConverter implements JSR310ToStringGenericConverter<LocalDate> { INSTANCE }
    enum LocalTimeToStringConverter implements JSR310ToStringGenericConverter<LocalTime> { INSTANCE }
}
