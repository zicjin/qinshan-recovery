package com.sunwayworld.framework.converter.string;

import com.sunwayworld.framework.converter.Converter;
import com.sunwayworld.framework.converter.ConverterFactory;
import com.sunwayworld.framework.converter.exception.ConverterNotFoundException;
import java.io.BufferedReader;
import java.io.CharArrayReader;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;

public enum StringToReaderConverterFactory implements ConverterFactory<String, Reader> {
    INSTANCE;

    public <T extends Reader> Converter<String, T> getConverter(Class<T> targetType) {
        return new StringToReaderConverter<T>(targetType);
    }

    class StringToReaderConverter<T extends Reader> implements Converter<String, T> {
        private final Class<T> readerType;

        public StringToReaderConverter(Class<T> readerType) { this.readerType = readerType; }
        public Class<String> getSourceType() { return String.class; }
        public Class<T> getTargetType() { return readerType; }

        @SuppressWarnings("unchecked")
        public T convert(String source) {
            if (source == null) return null;
            if (readerType == BufferedReader.class)
                return (T) new BufferedReader(new StringReader(source));
            if (readerType == CharArrayReader.class)
                return (T) new CharArrayReader(source.toCharArray());
            if (readerType == InputStreamReader.class)
                return (T) new InputStreamReader(new java.io.ByteArrayInputStream(source.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);
            if (readerType == LineNumberReader.class)
                return (T) new LineNumberReader(new StringReader(source));
            if (readerType == StringReader.class)
                return (T) new StringReader(source);
            throw new ConverterNotFoundException(getSourceType(), readerType);
        }
    }
}
