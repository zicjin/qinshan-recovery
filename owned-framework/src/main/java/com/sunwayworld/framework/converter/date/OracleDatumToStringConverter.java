package com.sunwayworld.framework.converter.date;

import com.sunwayworld.framework.converter.Converter;
import com.sunwayworld.framework.converter.exception.ConvertFailedException;
import com.sunwayworld.framework.converter.exception.ConverterNotFoundException;
import com.sunwayworld.owned.internal.context.OracleDateValues;
import com.sunwayworld.owned.internal.converter.DateTimeState;
import java.util.Date;
import oracle.sql.Datum;
import oracle.sql.DATE;
import oracle.sql.TIMESTAMP;
import oracle.sql.TIMESTAMPLTZ;
import oracle.sql.TIMESTAMPTZ;

public interface OracleDatumToStringConverter<S extends Datum> extends Converter<S,String> {
    @SuppressWarnings("unchecked") default Class<S> getSourceType() {
        return (Class<S>) ((java.lang.reflect.ParameterizedType)getClass().getGenericInterfaces()[0]).getActualTypeArguments()[0];
    }
    default Class<String> getTargetType() { return String.class; }
    default String convert(S source) {
        Class<S> type = getSourceType();
        if (type.equals(DATE.class)) return DateTimeState.formatDateTime(new Date(((DATE)source).dateValue().getTime()));
        if (!Datum.class.isAssignableFrom(type)) throw new ConverterNotFoundException(type, String.class);
        try { return DateTimeState.formatDateTime(new Date(OracleDateValues.read(source, type).getTime())); }
        catch (java.sql.SQLException e) { throw new ConvertFailedException(type, String.class, e); }
    }
    enum OracleDateToStringConverter implements OracleDatumToStringConverter<DATE> { INSTANCE }
    enum OracleTimestampToStringConverter implements OracleDatumToStringConverter<TIMESTAMP> { INSTANCE }
    enum OracleTimestamptzToStringConverter implements OracleDatumToStringConverter<TIMESTAMPTZ> { INSTANCE }
    enum OracleTimestampltzToStringConverter implements OracleDatumToStringConverter<TIMESTAMPLTZ> { INSTANCE }
}
