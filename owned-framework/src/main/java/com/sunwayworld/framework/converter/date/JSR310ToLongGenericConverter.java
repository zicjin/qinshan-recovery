package com.sunwayworld.framework.converter.date;

import com.sunwayworld.framework.converter.Converter;
import com.sunwayworld.framework.converter.exception.ConverterNotFoundException;
import java.lang.reflect.ParameterizedType;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.Temporal;
import java.util.Arrays;

public interface JSR310ToLongGenericConverter<S extends Temporal> extends Converter<S, Long> {
    @SuppressWarnings("unchecked")
    default Class<S> getSourceType() {
        ParameterizedType type = (ParameterizedType) Arrays.asList(getClass().getGenericInterfaces()).stream().findFirst().get();
        return (Class<S>) type.getActualTypeArguments()[0];
    }

    default Class<Long> getTargetType() {
        return Long.class;
    }

    default Long convert(S source) {
        if (source == null) return null;
        Class<S> sourceType = getSourceType();
        if (LocalDateTime.class.equals(sourceType))
            return Long.valueOf(((LocalDateTime) source).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
        if (LocalDate.class.equals(sourceType))
            return Long.valueOf(((LocalDate) source).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
        if (LocalTime.class.equals(sourceType))
            return Long.valueOf(java.sql.Time.valueOf((LocalTime) source).getTime());
        throw new ConverterNotFoundException(getSourceType(), Long.class);
    }

    enum LocalDateToLongConverter implements JSR310ToLongGenericConverter<LocalDate> {
        INSTANCE;
    }

    enum LocalDateTimeToLongConverter implements JSR310ToLongGenericConverter<LocalDateTime> {
        INSTANCE;
    }

    enum LocalTimeToLongConverter implements JSR310ToLongGenericConverter<LocalTime> {
        INSTANCE;
    }
}
