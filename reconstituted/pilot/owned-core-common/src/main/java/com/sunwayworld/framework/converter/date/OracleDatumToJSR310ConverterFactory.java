/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.converter.date;

import com.sunwayworld.framework.converter.Converter;
import com.sunwayworld.framework.converter.ConverterFactory;
import com.sunwayworld.framework.converter.exception.ConvertFailedException;
import com.sunwayworld.framework.converter.exception.ConverterNotFoundException;
import com.sunwayworld.framework.mybatis.MybatisHelper;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.Temporal;
import oracle.jdbc.OracleConnection;
import oracle.sql.DATE;
import oracle.sql.Datum;
import oracle.sql.TIMESTAMP;
import oracle.sql.TIMESTAMPLTZ;
import oracle.sql.TIMESTAMPTZ;

public enum OracleDatumToJSR310ConverterFactory implements ConverterFactory<Datum, Temporal>
{
    INSTANCE;


    @Override
    public <T extends Temporal> Converter<Datum, T> getConverter(Class<T> targetType) {
        return new OracleDatumToJSR310Converter<T>(targetType);
    }

    private class OracleDatumToJSR310Converter<T extends Temporal>
    implements Converter<Datum, T> {
        private final Class<T> jsr310Type;

        public OracleDatumToJSR310Converter(Class<T> jsr310Type) {
            this.jsr310Type = jsr310Type;
        }

        @Override
        public Class<Datum> getSourceType() {
            return Datum.class;
        }

        @Override
        public Class<T> getTargetType() {
            return this.jsr310Type;
        }

        @Override
        public T convert(Datum source) {
            Connection conn;
            if (source == null) {
                return null;
            }
            Class<T> targetType = this.getTargetType();
            Class<?> sourceType = source.getClass();
            Date value = null;
            if (TIMESTAMP.class.equals(sourceType)) {
                try {
                    value = new Date(((TIMESTAMP)source).dateValue().getTime());
                }
                catch (SQLException sqle) {
                    throw new ConvertFailedException(sourceType, String.class, sqle);
                }
            } else if (TIMESTAMPTZ.class.equals(sourceType)) {
                try {
                    conn = MybatisHelper.getConnection();
                    if (!OracleConnection.class.equals(conn.getClass())) {
                        conn = conn.unwrap(OracleConnection.class);
                    }
                    value = new Date(((TIMESTAMPTZ)source).dateValue(conn).getTime());
                }
                catch (SQLException sqle) {
                    throw new ConvertFailedException(sourceType, String.class, sqle);
                }
            } else if (TIMESTAMPLTZ.class.equals(sourceType)) {
                try {
                    conn = MybatisHelper.getConnection();
                    if (!OracleConnection.class.equals(conn.getClass())) {
                        conn = conn.unwrap(OracleConnection.class);
                    }
                    value = new Date(((TIMESTAMPLTZ)source).dateValue(conn).getTime());
                }
                catch (SQLException sqle) {
                    throw new ConvertFailedException(sourceType, String.class, sqle);
                }
            } else if (DATE.class.equals(sourceType)) {
                value = new Date(((DATE)source).dateValue().getTime());
            } else if (Datum.class.isAssignableFrom(sourceType)) {
                try {
                    value = source.dateValue();
                }
                catch (SQLException sqle) {
                    throw new ConvertFailedException(sourceType, String.class, sqle);
                }
            } else {
                throw new ConverterNotFoundException(sourceType, String.class);
            }
            ZonedDateTime zondedDateTime = new java.util.Date(value.getTime()).toInstant().atZone(ZoneId.systemDefault());
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
