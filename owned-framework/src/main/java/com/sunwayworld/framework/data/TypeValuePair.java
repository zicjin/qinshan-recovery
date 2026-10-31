package com.sunwayworld.framework.data;

public class TypeValuePair<T> extends Pair<Class<T>, T> {
    private static final long serialVersionUID = 1L;
    public TypeValuePair(Class<T> type, T value) { super(type, value); }
}
