/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.converter.date;

import com.sunwayworld.framework.converter.Converter;
import com.sunwayworld.framework.converter.exception.ConverterNotFoundException;
import java.lang.reflect.ParameterizedType;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.Temporal;

public interface JSR310ToLongGenericConverter<S extends Temporal>
extends Converter<S, Long> {
    @Override
    default public Class<S> getSourceType() {
        return (Class)((ParameterizedType)this.getClass().getGenericInterfaces()[0]).getActualTypeArguments()[0];
    }

    @Override
    default public Class<Long> getTargetType() {
        return Long.class;
    }

    @Override
    default public Long convert(S source) {
        if (source == null) {
            return null;
        }
        Class<S> sourceType = this.getSourceType();
        if (LocalDateTime.class.equals(sourceType)) {
            return ((LocalDateTime)source).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        }
        if (LocalDate.class.equals(sourceType)) {
            return ((LocalDate)source).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        }
        if (LocalTime.class.equals(sourceType)) {
            return Time.valueOf((LocalTime)source).getTime();
        }
        throw new ConverterNotFoundException(this.getSourceType(), Long.class);
    }

    public static enum LocalTimeToLongConverter implements JSR310ToLongGenericConverter<LocalTime>
    {
        INSTANCE;

    }

    public static enum LocalDateToLongConverter implements JSR310ToLongGenericConverter<LocalDate>
    {
        INSTANCE;

    }

    public static enum LocalDateTimeToLongConverter implements JSR310ToLongGenericConverter<LocalDateTime>
    {
        INSTANCE;

    }
}
