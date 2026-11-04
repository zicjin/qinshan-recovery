/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.converter.bool;

import com.sunwayworld.framework.converter.Converter;

public enum BooleanToStringConverter implements Converter<Boolean, String>
{
    INSTANCE;


    @Override
    public String convert(Boolean source) {
        if (source == null) {
            return null;
        }
        return source.toString();
    }
}
