/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.converter;

import java.lang.reflect.ParameterizedType;
import java.util.Arrays;

public interface Convertable<S, T> {
    default public Class<S> getSourceType() {
        return (Class)((ParameterizedType)Arrays.asList(this.getClass().getGenericInterfaces()).stream().findFirst().get()).getActualTypeArguments()[0];
    }

    default public Class<T> getTargetType() {
        return (Class)((ParameterizedType)Arrays.asList(this.getClass().getGenericInterfaces()).stream().findFirst().get()).getActualTypeArguments()[1];
    }

    default public boolean canConvert(Class<?> sourceType, Class<?> targetType) {
        return this.getSourceType().isAssignableFrom(sourceType) && this.getTargetType().isAssignableFrom(targetType);
    }
}
