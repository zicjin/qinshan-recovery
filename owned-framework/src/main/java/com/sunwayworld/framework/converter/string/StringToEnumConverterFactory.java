package com.sunwayworld.framework.converter.string;

import com.sunwayworld.framework.converter.Converter;
import com.sunwayworld.framework.converter.ConverterFactory;

public enum StringToEnumConverterFactory implements ConverterFactory<String, Enum> {
    INSTANCE;

    public <T extends Enum> Converter<String, T> getConverter(Class<T> targetType) {
        return new StringToEnumConverter<T>(targetType);
    }

    class StringToEnumConverter<T extends Enum> implements Converter<String, T> {
        private final Class<T> enumType;

        public StringToEnumConverter(Class<T> enumType) {
            this.enumType = enumType;
        }

        public Class<String> getSourceType() {
            return String.class;
        }

        public Class<T> getTargetType() {
            return enumType;
        }

        @SuppressWarnings("unchecked")
        public T convert(String source) {
            if (source == null || source.length() == 0) return null;
            return (T) Enum.valueOf(enumType, source.trim());
        }
    }
}
