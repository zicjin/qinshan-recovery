/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.converter.date;

import com.sunwayworld.framework.converter.Converter;
import com.sunwayworld.framework.converter.ConverterFactory;
import com.sunwayworld.framework.utils.ConvertUtils;
import java.time.temporal.Temporal;

public enum JSR310ToJSR310ConverterFactory implements ConverterFactory<Temporal, Temporal>
{
    INSTANCE;


    @Override
    public <T extends Temporal> Converter<Temporal, T> getConverter(Class<T> targetType) {
        return new JSR310ToJSR310Converter<T>(targetType);
    }

    private class JSR310ToJSR310Converter<T extends Temporal>
    implements Converter<Temporal, T> {
        private final Class<T> jsr310Type;

        public JSR310ToJSR310Converter(Class<T> jsr310Type) {
            this.jsr310Type = jsr310Type;
        }

        @Override
        public Class<Temporal> getSourceType() {
            return Temporal.class;
        }

        @Override
        public Class<T> getTargetType() {
            return this.jsr310Type;
        }

        @Override
        public T convert(Temporal source) {
            if (source == null) {
                return null;
            }
            long l = ConvertUtils.convert(source, Long.TYPE);
            return (T)((Temporal)ConvertUtils.convert(l, this.getTargetType()));
        }
    }
}
