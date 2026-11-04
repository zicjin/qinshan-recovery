/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.converter.bool;

import com.sunwayworld.framework.converter.Converter;
import com.sunwayworld.framework.converter.ConverterFactory;
import com.sunwayworld.framework.utils.NumberUtils;

public enum BooleanToNumberConverterFactory implements ConverterFactory<Boolean, Number>
{
    INSTANCE;


    @Override
    public <T extends Number> Converter<Boolean, T> getConverter(Class<T> targetType) {
        return new BooleanToNumberConverter<T>(targetType);
    }

    private class BooleanToNumberConverter<T extends Number>
    implements Converter<Boolean, T> {
        private final Class<T> numberType;

        public BooleanToNumberConverter(Class<T> numberType) {
            this.numberType = numberType;
        }

        @Override
        public Class<Boolean> getSourceType() {
            return Boolean.class;
        }

        @Override
        public Class<T> getTargetType() {
            return this.numberType;
        }

        @Override
        public T convert(Boolean source) {
            if (source == null) {
                return null;
            }
            if (Boolean.TRUE.equals(source)) {
                return NumberUtils.parseNumber("1", this.getTargetType());
            }
            return NumberUtils.parseNumber("0", this.getTargetType());
        }
    }
}
