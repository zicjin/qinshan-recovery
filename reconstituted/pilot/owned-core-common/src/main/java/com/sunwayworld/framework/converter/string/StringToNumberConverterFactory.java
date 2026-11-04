/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.converter.string;

import com.sunwayworld.framework.converter.Converter;
import com.sunwayworld.framework.converter.ConverterFactory;
import com.sunwayworld.framework.utils.NumberUtils;

public enum StringToNumberConverterFactory implements ConverterFactory<String, Number>
{
    INSTANCE;


    @Override
    public <T extends Number> Converter<String, T> getConverter(Class<T> targetType) {
        return new StringToNumberConverter<T>(targetType);
    }

    private class StringToNumberConverter<T extends Number>
    implements Converter<String, T> {
        private final Class<T> numberType;

        public StringToNumberConverter(Class<T> numberType) {
            this.numberType = numberType;
        }

        @Override
        public Class<String> getSourceType() {
            return String.class;
        }

        @Override
        public Class<T> getTargetType() {
            return this.numberType;
        }

        @Override
        public T convert(String source) {
            return NumberUtils.parseNumber(source, this.getTargetType());
        }
    }
}
