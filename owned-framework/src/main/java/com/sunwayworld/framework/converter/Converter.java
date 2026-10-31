package com.sunwayworld.framework.converter;

public interface Converter<S, T> extends Convertable<S, T> {
    T convert(S source);
}
