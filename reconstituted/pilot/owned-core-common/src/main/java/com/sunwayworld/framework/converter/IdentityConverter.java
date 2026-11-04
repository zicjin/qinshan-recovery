/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.converter;

import com.sunwayworld.framework.converter.Converter;

public class IdentityConverter<T, S extends T>
implements Converter<S, T> {
    @Override
    public T convert(S source) {
        return (T)source;
    }
}
