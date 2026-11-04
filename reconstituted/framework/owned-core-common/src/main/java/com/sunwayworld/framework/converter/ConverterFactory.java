/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.converter;

import com.sunwayworld.framework.converter.Convertable;
import com.sunwayworld.framework.converter.Converter;

public interface ConverterFactory<S, R>
extends Convertable<S, R> {
    public <T extends R> Converter<S, T> getConverter(Class<T> var1);
}
