/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.converter.number;

import com.sunwayworld.framework.converter.Converter;

public enum NumberToBooleanConverter implements Converter<Number, Boolean>
{
    INSTANCE;


    @Override
    public Class<Number> getSourceType() {
        return Number.class;
    }

    @Override
    public Class<Boolean> getTargetType() {
        return Boolean.class;
    }

    @Override
    public Boolean convert(Number source) {
        if (source == null) {
            return false;
        }
        Number n = source;
        return n.doubleValue() > 0.0;
    }
}
