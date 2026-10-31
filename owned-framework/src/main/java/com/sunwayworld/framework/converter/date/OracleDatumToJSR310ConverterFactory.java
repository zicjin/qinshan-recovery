package com.sunwayworld.framework.converter.date;

import com.sunwayworld.framework.converter.Converter;
import com.sunwayworld.framework.converter.ConverterFactory;
import com.sunwayworld.framework.converter.exception.ConvertFailedException;
import com.sunwayworld.framework.converter.exception.ConverterNotFoundException;
import com.sunwayworld.owned.internal.context.OracleDateValues;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.Temporal;
import oracle.sql.Datum;
import oracle.sql.DATE;

public enum OracleDatumToJSR310ConverterFactory implements ConverterFactory<Datum, Temporal> {
    INSTANCE;
    public <T extends Temporal> Converter<Datum, T> getConverter(Class<T> targetType) {
        return new OracleDatumToJSR310Converter<T>(targetType);
    }
    class OracleDatumToJSR310Converter<T extends Temporal> implements Converter<Datum,T> {
        private final Class<T> target;
        public OracleDatumToJSR310Converter(Class<T> target) { this.target = target; }
        public Class<Datum> getSourceType() { return Datum.class; }
        public Class<T> getTargetType() { return target; }
        public T convert(Datum source) {
            if (source == null) return null;
            Class<?> actual = source.getClass();
            java.sql.Date value;
            if (actual == DATE.class) value = new java.sql.Date(((DATE)source).dateValue().getTime());
            else try { value = OracleDateValues.read(source, actual); }
            catch (java.sql.SQLException e) { throw new ConvertFailedException(actual, String.class, e); }
            ZonedDateTime zdt = new java.util.Date(value.getTime()).toInstant().atZone(ZoneId.systemDefault());
            if (target == LocalDateTime.class) return target.cast(zdt.toLocalDateTime());
            if (target == LocalDate.class) return target.cast(zdt.toLocalDate());
            if (target == LocalTime.class) return target.cast(zdt.toLocalTime());
            throw new ConverterNotFoundException(getSourceType(), target);
        }
    }
}
