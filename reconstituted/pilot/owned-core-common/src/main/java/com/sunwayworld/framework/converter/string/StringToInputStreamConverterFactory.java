/*
 * Decompiled with CFR 0.152.
 */
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

public enum StringToInputStreamConverterFactory implements ConverterFactory<String, InputStream>
{
    INSTANCE;


    @Override
    public <T extends InputStream> Converter<String, T> getConverter(Class<T> targetType) {
        return new StringToInputStreamConverter<T>(targetType);
    }

    private class StringToInputStreamConverter<T extends InputStream>
    implements Converter<String, T> {
        private final Class<T> inputStreamType;

        public StringToInputStreamConverter(Class<T> inputStreamType) {
            this.inputStreamType = inputStreamType;
        }

        @Override
        public Class<String> getSourceType() {
            return String.class;
        }

        @Override
        public Class<T> getTargetType() {
            return this.inputStreamType;
        }

        @Override
        public T convert(String source) {
            if (source == null) {
                return null;
            }
            Class<T> targetType = this.getTargetType();
            if (ByteArrayInputStream.class == targetType) {
                return (T)new ByteArrayInputStream(source.getBytes(StandardCharsets.UTF_8));
            }
            if (BufferedInputStream.class == targetType) {
                return (T)new BufferedInputStream(new ByteArrayInputStream(source.getBytes(StandardCharsets.UTF_8)));
            }
            if (DataInputStream.class == targetType) {
                return (T)new DataInputStream(new ByteArrayInputStream(source.getBytes(StandardCharsets.UTF_8)));
            }
            if (PushbackInputStream.class == targetType) {
                return (T)new PushbackInputStream(new ByteArrayInputStream(source.getBytes(StandardCharsets.UTF_8)));
            }
            throw new ConverterNotFoundException(this.getSourceType(), targetType);
        }
    }
}
