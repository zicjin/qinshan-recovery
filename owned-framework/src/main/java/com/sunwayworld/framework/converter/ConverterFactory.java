package com.sunwayworld.framework.converter;

public interface ConverterFactory<S, R> extends Convertable<S, R> {
    <T extends R> Converter<S, T> getConverter(Class<T> targetType);
}
