/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.converter.string;

import com.sunwayworld.framework.converter.Converter;
import com.sunwayworld.framework.converter.exception.ConvertFailedException;

public enum StringToBooleanConverter implements Converter<String, Boolean>
{
    INSTANCE;


    @Override
    public Boolean convert(String source) {
        if (source == null) {
            return false;
        }
        if ("true".equalsIgnoreCase(source) || "yes".equalsIgnoreCase(source) || "on".equalsIgnoreCase(source) || "1".equalsIgnoreCase(source)) {
            return Boolean.TRUE;
        }
        if ("false".equalsIgnoreCase(source) || "no".equalsIgnoreCase(source) || "off".equalsIgnoreCase(source) || "0".equalsIgnoreCase(source)) {
            return Boolean.FALSE;
        }
        throw new ConvertFailedException(this.getSourceType(), this.getTargetType(), source);
    }
}
