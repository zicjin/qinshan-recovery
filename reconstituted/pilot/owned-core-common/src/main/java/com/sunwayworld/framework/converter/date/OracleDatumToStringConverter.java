/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.converter.date;

import com.sunwayworld.framework.converter.Converter;
import com.sunwayworld.framework.converter.exception.ConvertFailedException;
import com.sunwayworld.framework.converter.exception.ConverterNotFoundException;
import com.sunwayworld.framework.mybatis.MybatisHelper;
import com.sunwayworld.framework.utils.DateTimeUtils;
import java.lang.reflect.ParameterizedType;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import oracle.jdbc.OracleConnection;
import oracle.sql.DATE;
import oracle.sql.Datum;
import oracle.sql.TIMESTAMP;
import oracle.sql.TIMESTAMPLTZ;
import oracle.sql.TIMESTAMPTZ;

public interface OracleDatumToStringConverter<S extends Datum>
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
        Class<S> sourceType = this.getSourceType();
        if (TIMESTAMP.class.equals(sourceType)) {
            try {
                return DateTimeUtils.formatDateTime(new Date(((TIMESTAMP)source).dateValue().getTime()));
            }
            catch (SQLException sqle) {
                throw new ConvertFailedException(sourceType, String.class, sqle);
            }
        }
        if (TIMESTAMPTZ.class.equals(sourceType)) {
            try {
                Connection conn = MybatisHelper.getConnection();
                if (!OracleConnection.class.equals(conn.getClass())) {
                    conn = conn.unwrap(OracleConnection.class);
                }
                return DateTimeUtils.formatDateTime(new java.util.Date(((TIMESTAMPTZ)source).dateValue(conn).getTime()));
            }
            catch (SQLException sqle) {
                throw new ConvertFailedException(sourceType, String.class, sqle);
            }
        }
        if (TIMESTAMPLTZ.class.equals(sourceType)) {
            try {
                Connection conn = MybatisHelper.getConnection();
                if (!OracleConnection.class.equals(conn.getClass())) {
                    conn = conn.unwrap(OracleConnection.class);
                }
                return DateTimeUtils.formatDateTime(new java.util.Date(((TIMESTAMPLTZ)source).dateValue(conn).getTime()));
            }
            catch (SQLException sqle) {
                throw new ConvertFailedException(sourceType, String.class, sqle);
            }
        }
        if (DATE.class.equals(sourceType)) {
            return DateTimeUtils.formatDateTime(new java.util.Date(((DATE)source).dateValue().getTime()));
        }
        if (Datum.class.isAssignableFrom(sourceType)) {
            try {
                return DateTimeUtils.formatDateTime(new java.util.Date(((Datum)source).dateValue().getTime()));
            }
            catch (SQLException sqle) {
                throw new ConvertFailedException(sourceType, String.class, sqle);
            }
        }
        throw new ConverterNotFoundException(sourceType, String.class);
    }

    public static enum OracleTimestampltzToStringConverter implements OracleDatumToStringConverter<TIMESTAMPLTZ>
    {
        INSTANCE;

    }

    public static enum OracleTimestamptzToStringConverter implements OracleDatumToStringConverter<TIMESTAMPTZ>
    {
        INSTANCE;

    }

    public static enum OracleTimestampToStringConverter implements OracleDatumToStringConverter<TIMESTAMP>
    {
        INSTANCE;

    }

    public static enum OracleDateToStringConverter implements OracleDatumToStringConverter<DATE>
    {
        INSTANCE;

    }
}
