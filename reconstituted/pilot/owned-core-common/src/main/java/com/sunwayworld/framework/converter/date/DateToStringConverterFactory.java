/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.converter.date;

import com.sunwayworld.framework.converter.Converter;
import com.sunwayworld.framework.utils.DateTimeUtils;
import java.lang.reflect.ParameterizedType;
import java.sql.Time;
import java.util.Date;

public interface DateToStringConverterFactory<S extends Date>
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
        if (Time.class.equals(sourceType)) {
            return DateTimeUtils.formatTime(source);
        }
        return DateTimeUtils.formatDateTime(source);
    }

    public static enum TimeToStringConverter implements DateToStringConverterFactory<Time>
    {
        INSTANCE;

    }

    public static enum DateToStringConverter implements DateToStringConverterFactory<Date>
    {
        INSTANCE;

    }
}
