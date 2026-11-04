/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.converter.date;

import com.sunwayworld.framework.converter.Converter;
import com.sunwayworld.framework.converter.exception.ConverterNotFoundException;
import com.sunwayworld.framework.utils.DateTimeUtils;
import java.lang.reflect.ParameterizedType;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.Temporal;

public interface JSR310ToStringGenericConverter<S extends Temporal>
extends Converter<S, String> {
    @Override
    default public Class<S> getSourceType() {
        return (Class)((ParameterizedType)this.getClass().getGenericInterfaces()[0]).getActualTypeArguments()[0];
    }

    @Override
    default public Class<String> getTargetType() {
        return String.class;
    }

    @Override
    default public String convert(S source) {
        if (source == null) {
            return null;
        }
        Class<S> sourceType = this.getSourceType();
        if (LocalDateTime.class.equals(sourceType)) {
            return DateTimeUtils.formatLocalDateTime((LocalDateTime)source);
        }
        if (LocalDate.class.equals(sourceType)) {
            return DateTimeUtils.formatLocalDate((LocalDate)source);
        }
        if (LocalTime.class.equals(sourceType)) {
            return DateTimeUtils.formatLocalTime((LocalTime)source);
        }
        throw new ConverterNotFoundException(sourceType, this.getTargetType());
    }

    public static enum LocalTimeToStringConverter implements JSR310ToStringGenericConverter<LocalTime>
    {
        INSTANCE;

    }

    public static enum LocalDateToStringConverter implements JSR310ToStringGenericConverter<LocalDate>
    {
        INSTANCE;

    }

    public static enum LocalDateTimeToStringConverter implements JSR310ToStringGenericConverter<LocalDateTime>
    {
        INSTANCE;

    }
}
