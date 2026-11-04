/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.converter;

import com.sunwayworld.framework.converter.Convertable;

public interface Converter<S, T>
extends Convertable<S, T> {
    public T convert(S var1);
}
