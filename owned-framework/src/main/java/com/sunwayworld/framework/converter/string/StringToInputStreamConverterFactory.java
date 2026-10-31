package com.sunwayworld.framework.converter.string;

import com.sunwayworld.framework.converter.Converter;
import com.sunwayworld.framework.converter.ConverterFactory;
import com.sunwayworld.framework.converter.exception.ConverterNotFoundException;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.nio.charset.StandardCharsets;

public enum StringToInputStreamConverterFactory implements ConverterFactory<String, InputStream> {
    INSTANCE;

    public <T extends InputStream> Converter<String, T> getConverter(Class<T> targetType) {
        return new StringToInputStreamConverter<T>(targetType);
    }

    class StringToInputStreamConverter<T extends InputStream> implements Converter<String, T> {
        private final Class<T> inputStreamType;

        public StringToInputStreamConverter(Class<T> inputStreamType) { this.inputStreamType = inputStreamType; }
        public Class<String> getSourceType() { return String.class; }
        public Class<T> getTargetType() { return inputStreamType; }

        @SuppressWarnings("unchecked")
        public T convert(String source) {
            if (source == null) return null;
            byte[] bytes = source.getBytes(StandardCharsets.UTF_8);
            if (inputStreamType == ByteArrayInputStream.class) return (T) new ByteArrayInputStream(bytes);
            if (inputStreamType == BufferedInputStream.class) return (T) new BufferedInputStream(new ByteArrayInputStream(bytes));
            if (inputStreamType == DataInputStream.class) return (T) new DataInputStream(new ByteArrayInputStream(bytes));
            if (inputStreamType == PushbackInputStream.class) return (T) new PushbackInputStream(new ByteArrayInputStream(bytes));
            throw new ConverterNotFoundException(getSourceType(), inputStreamType);
        }
    }
}
