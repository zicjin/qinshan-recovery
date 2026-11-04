/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.converter.string;

import com.sunwayworld.framework.converter.Converter;
import com.sunwayworld.framework.converter.ConverterFactory;
import com.sunwayworld.framework.converter.exception.ConvertFailedException;
import com.sunwayworld.framework.converter.exception.ConverterNotFoundException;
import com.sunwayworld.framework.utils.ClassUtils;
import com.sunwayworld.framework.utils.DateTimeUtils;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;

@Deprecated
public enum StringToDateConverterFactory implements ConverterFactory<String, Date>
{
    INSTANCE;


    @Override
    public <T extends Date> Converter<String, T> getConverter(Class<T> targetType) {
        return new StringToDateConverter<T>(targetType);
    }

    private class StringToDateConverter<T extends Date>
    implements Converter<String, T> {
        private final Class<T> dateType;

        public StringToDateConverter(Class<T> dateType) {
            this.dateType = dateType;
        }

        @Override
        public Class<String> getSourceType() {
            return String.class;
        }

        @Override
        public Class<T> getTargetType() {
            return this.dateType;
        }

        @Override
        public T convert(String source) {
            Date date = DateTimeUtils.parseDate(source);
            if (date == null) {
                return null;
            }
            Constructor<T> constructor = ClassUtils.getConstructorIfAvailable(this.dateType, Long.class);
            if (constructor != null) {
                try {
                    return (T)((Date)constructor.newInstance(date.getTime()));
                }
                catch (IllegalAccessException | IllegalArgumentException | InstantiationException | InvocationTargetException e) {
                    throw new ConvertFailedException(this.getSourceType(), this.getTargetType(), e);
                }
            }
            throw new ConverterNotFoundException(this.getSourceType(), this.getTargetType());
        }
    }
}
