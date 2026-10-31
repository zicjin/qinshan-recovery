package com.sunwayworld.framework.converter.date;

import com.sunwayworld.framework.converter.Converter;
import com.sunwayworld.framework.converter.exception.ConvertFailedException;
import com.sunwayworld.framework.converter.exception.ConverterNotFoundException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.Temporal;
import java.util.Date;

public interface JSR310ToDateGenericConverter<S extends Temporal, T extends Date> extends Converter<S, T> {
    default T convert(S source) {
        if (source == null) return null;
        Class<T> targetType = getTargetType();
        Constructor<T> constructor;
        try {
            constructor = targetType.getConstructor(long.class);
        } catch (NoSuchMethodException missing) {
            constructor = null;
        }
        Class<S> sourceType = getSourceType();
        if (constructor == null) throw new ConverterNotFoundException(getSourceType(), targetType);
        try {
            long millis;
            if (LocalDateTime.class.equals(sourceType))
                millis = ((LocalDateTime) source).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
            else if (LocalDate.class.equals(sourceType))
                millis = ((LocalDate) source).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
            else if (LocalTime.class.equals(sourceType))
                millis = java.sql.Time.valueOf((LocalTime) source).getTime();
            else throw new ConverterNotFoundException(getSourceType(), targetType);
            return constructor.newInstance(Long.valueOf(millis));
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException failure) {
            throw new ConvertFailedException(getSourceType(), targetType, failure);
        }
    }

    enum LocalDateToDateConverter implements JSR310ToDateGenericConverter<LocalDate, Date> { INSTANCE; }
    enum LocalDateToSqlDateConverter implements JSR310ToDateGenericConverter<LocalDate, java.sql.Date> { INSTANCE; }
    enum LocalDateToSqlTimeConverter implements JSR310ToDateGenericConverter<LocalDate, java.sql.Time> { INSTANCE; }
    enum LocalDateToSqlTimestampConverter implements JSR310ToDateGenericConverter<LocalDate, java.sql.Timestamp> { INSTANCE; }
    enum LocalDateTimeToDateConverter implements JSR310ToDateGenericConverter<LocalDateTime, Date> { INSTANCE; }
    enum LocalDateTimeToSqlDateConverter implements JSR310ToDateGenericConverter<LocalDateTime, java.sql.Date> { INSTANCE; }
    enum LocalDateTimeToSqlTimeConverter implements JSR310ToDateGenericConverter<LocalDateTime, java.sql.Time> { INSTANCE; }
    enum LocalDateTimeToSqlTimestampConverter implements JSR310ToDateGenericConverter<LocalDateTime, java.sql.Timestamp> { INSTANCE; }
    enum LocalTimeToDateConverter implements JSR310ToDateGenericConverter<LocalTime, Date> { INSTANCE; }
    enum LocalTimeToSqlDateConverter implements JSR310ToDateGenericConverter<LocalTime, java.sql.Date> { INSTANCE; }
    enum LocalTimeToSqlTimeConverter implements JSR310ToDateGenericConverter<LocalTime, java.sql.Time> { INSTANCE; }
    enum LocalTimeToSqlTimestampConverter implements JSR310ToDateGenericConverter<LocalTime, java.sql.Timestamp> { INSTANCE; }
}
