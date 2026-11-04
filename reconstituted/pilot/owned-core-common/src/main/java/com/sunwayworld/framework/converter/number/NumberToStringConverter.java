/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.converter.number;

import com.sunwayworld.framework.converter.Converter;
import com.sunwayworld.framework.utils.NumberUtils;

public enum NumberToStringConverter implements Converter<Number, String>
{
    INSTANCE;


    @Override
    public String convert(Number source) {
        return NumberUtils.formatNumber(source);
    }
}
