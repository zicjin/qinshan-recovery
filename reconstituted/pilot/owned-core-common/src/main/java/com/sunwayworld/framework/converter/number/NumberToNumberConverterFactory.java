/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.converter.number;

import com.sunwayworld.framework.converter.Converter;
import com.sunwayworld.framework.converter.ConverterFactory;
import com.sunwayworld.framework.converter.exception.ConverterNotFoundException;
import java.math.BigDecimal;
import java.math.BigInteger;

public enum NumberToNumberConverterFactory implements ConverterFactory<Number, Number>
{
    INSTANCE;


    @Override
    public <T extends Number> Converter<Number, T> getConverter(Class<T> targetType) {
        return new NumberToNumberConverter<T>(targetType);
    }

    private class NumberToNumberConverter<T extends Number>
    implements Converter<Number, T> {
        private final Class<T> numberType;

        public NumberToNumberConverter(Class<T> numberType) {
            this.numberType = numberType;
        }

        @Override
        public Class<Number> getSourceType() {
            return Number.class;
        }

        @Override
        public Class<T> getTargetType() {
            return this.numberType;
        }

        @Override
        public T convert(Number source) {
            if (source == null) {
                return null;
            }
            Class<T> targetType = this.getTargetType();
            if (Byte.class == targetType || Byte.TYPE == targetType) {
                return (T)Byte.valueOf(source.byteValue());
            }
            if (Short.class == targetType || Short.TYPE == targetType) {
                return (T)Short.valueOf(source.shortValue());
            }
            if (Integer.class == targetType || Integer.TYPE == targetType) {
                return (T)Integer.valueOf(source.intValue());
            }
            if (Long.class == targetType || Long.TYPE == targetType) {
                return (T)Long.valueOf(source.longValue());
            }
            if (Float.class == targetType || Float.TYPE == targetType) {
                return (T)Float.valueOf(source.floatValue());
            }
            if (Double.class == targetType || Double.TYPE == targetType) {
                return (T)Double.valueOf(source.doubleValue());
            }
            if (BigInteger.class == targetType) {
                return (T)BigInteger.valueOf(source.longValue());
            }
            if (BigDecimal.class == targetType) {
                return (T)BigDecimal.valueOf(source.doubleValue());
            }
            throw new ConverterNotFoundException(this.getSourceType(), targetType);
        }
    }
}
