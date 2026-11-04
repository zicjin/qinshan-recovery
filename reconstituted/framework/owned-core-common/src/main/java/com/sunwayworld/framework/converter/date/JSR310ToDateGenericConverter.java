/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.converter.date;

import com.sunwayworld.framework.converter.Converter;
import com.sunwayworld.framework.converter.exception.ConvertFailedException;
import com.sunwayworld.framework.converter.exception.ConverterNotFoundException;
import com.sunwayworld.framework.utils.ClassUtils;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.Temporal;

public interface JSR310ToDateGenericConverter<S extends Temporal, T extends java.util.Date>
extends Converter<S, T> {
    @Override
    default public T convert(S source) {
        if (source == null) {
            return null;
        }
        Class targetType = this.getTargetType();
        Constructor constructor = ClassUtils.getConstructorIfAvailable(targetType, Long.TYPE);
        Class sourceType = this.getSourceType();
        if (constructor != null) {
            try {
                long time;
                if (LocalDateTime.class.equals(sourceType)) {
                    time = ((LocalDateTime)source).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
                } else if (LocalDate.class.equals(sourceType)) {
                    time = ((LocalDate)source).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
                } else if (LocalTime.class.equals(sourceType)) {
                    time = Time.valueOf((LocalTime)source).getTime();
                } else {
                    throw new ConverterNotFoundException(this.getSourceType(), targetType);
                }
                return (T)((java.util.Date)constructor.newInstance(time));
            }
            catch (IllegalAccessException | IllegalArgumentException | InstantiationException | InvocationTargetException e) {
                throw new ConvertFailedException(this.getSourceType(), targetType, e);
            }
        }
        throw new ConverterNotFoundException(this.getSourceType(), targetType);
    }

    public static enum LocalTimeToSqlDateConverter implements JSR310ToDateGenericConverter<LocalTime, Date>
    {
        INSTANCE;

    }

    public static enum LocalTimeToSqlTimestampConverter implements JSR310ToDateGenericConverter<LocalTime, Timestamp>
    {
        INSTANCE;

    }

    public static enum LocalTimeToSqlTimeConverter implements JSR310ToDateGenericConverter<LocalTime, Time>
    {
        INSTANCE;

    }

    public static enum LocalTimeToDateConverter implements JSR310ToDateGenericConverter<LocalTime, java.util.Date>
    {
        INSTANCE;

    }

    public static enum LocalDateToSqlDateConverter implements JSR310ToDateGenericConverter<LocalDate, Date>
    {
        INSTANCE;

    }

    public static enum LocalDateToSqlTimestampConverter implements JSR310ToDateGenericConverter<LocalDate, Timestamp>
    {
        INSTANCE;

    }

    public static enum LocalDateToSqlTimeConverter implements JSR310ToDateGenericConverter<LocalDate, Time>
    {
        INSTANCE;

    }

    public static enum LocalDateToDateConverter implements JSR310ToDateGenericConverter<LocalDate, java.util.Date>
    {
        INSTANCE;

    }

    public static enum LocalDateTimeToSqlDateConverter implements JSR310ToDateGenericConverter<LocalDateTime, Date>
    {
        INSTANCE;

    }

    public static enum LocalDateTimeToSqlTimestampConverter implements JSR310ToDateGenericConverter<LocalDateTime, Timestamp>
    {
        INSTANCE;

    }

    public static enum LocalDateTimeToSqlTimeConverter implements JSR310ToDateGenericConverter<LocalDateTime, Time>
    {
        INSTANCE;

    }

    public static enum LocalDateTimeToDateConverter implements JSR310ToDateGenericConverter<LocalDateTime, java.util.Date>
    {
        INSTANCE;

    }
}
