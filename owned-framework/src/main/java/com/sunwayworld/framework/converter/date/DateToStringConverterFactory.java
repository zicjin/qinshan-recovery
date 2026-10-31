package com.sunwayworld.framework.converter.date;

import com.sunwayworld.framework.converter.Converter;
import com.sunwayworld.owned.internal.converter.DateTimeState;
import java.sql.Time;
import java.util.Date;

public interface DateToStringConverterFactory<S extends Date> extends Converter<S, String> {
    @SuppressWarnings("unchecked") default Class<S> getSourceType() { return (Class<S>) ((java.lang.reflect.ParameterizedType) getClass().getGenericInterfaces()[0]).getActualTypeArguments()[0]; }
    default Class<String> getTargetType() { return String.class; }
    default String convert(S source) {
        return Time.class.equals(getSourceType()) ? DateTimeState.formatTime(source) : DateTimeState.formatDateTime(source);
    }

    enum DateToStringConverter implements DateToStringConverterFactory<Date> { INSTANCE }
    enum TimeToStringConverter implements DateToStringConverterFactory<Time> { INSTANCE }
}
