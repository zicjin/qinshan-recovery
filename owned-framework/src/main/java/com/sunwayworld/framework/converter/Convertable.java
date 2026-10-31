package com.sunwayworld.framework.converter;

import java.lang.reflect.ParameterizedType;
import java.util.Arrays;

public interface Convertable<S, T> {
    @SuppressWarnings("unchecked")
    default Class<S> getSourceType() {
        ParameterizedType type = (ParameterizedType) Arrays.asList(getClass().getGenericInterfaces()).stream().findFirst().get();
        return (Class<S>) type.getActualTypeArguments()[0];
    }
    @SuppressWarnings("unchecked")
    default Class<T> getTargetType() {
        ParameterizedType type = (ParameterizedType) Arrays.asList(getClass().getGenericInterfaces()).stream().findFirst().get();
        return (Class<T>) type.getActualTypeArguments()[1];
    }
    default boolean canConvert(Class<?> sourceType, Class<?> targetType) {
        return getSourceType().isAssignableFrom(sourceType) && getTargetType().isAssignableFrom(targetType);
    }
}
