/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.converter.string;

import com.sunwayworld.framework.converter.Converter;
import com.sunwayworld.framework.converter.ConverterFactory;
import com.sunwayworld.framework.converter.exception.ConverterNotFoundException;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.CharArrayReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;

public enum StringToReaderConverterFactory implements ConverterFactory<String, Reader>
{
    INSTANCE;


    @Override
    public <T extends Reader> Converter<String, T> getConverter(Class<T> targetType) {
        return new StringToReaderConverter<T>(targetType);
    }

    private class StringToReaderConverter<T extends Reader>
    implements Converter<String, T> {
        private final Class<T> readerType;

        public StringToReaderConverter(Class<T> readerType) {
            this.readerType = readerType;
        }

        @Override
        public Class<String> getSourceType() {
            return String.class;
        }

        @Override
        public Class<T> getTargetType() {
            return this.readerType;
        }

        @Override
        public T convert(String source) {
            if (source == null) {
                return null;
            }
            Class<T> targetType = this.getTargetType();
            if (BufferedReader.class == targetType) {
                return (T)new BufferedReader(new StringReader(source));
            }
            if (CharArrayReader.class == this.getTargetType()) {
                return (T)new CharArrayReader(source.toCharArray());
            }
            if (InputStreamReader.class == targetType) {
                return (T)new InputStreamReader((InputStream)new ByteArrayInputStream(source.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);
            }
            if (LineNumberReader.class == targetType) {
                return (T)new LineNumberReader(new StringReader(source));
            }
            if (StringReader.class == targetType) {
                return (T)new StringReader(source);
            }
            throw new ConverterNotFoundException(this.getSourceType(), targetType);
        }
    }
}
