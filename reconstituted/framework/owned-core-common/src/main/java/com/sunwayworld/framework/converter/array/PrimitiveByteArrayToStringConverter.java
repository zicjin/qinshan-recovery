/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.converter.array;

import com.sunwayworld.framework.converter.Converter;

public enum PrimitiveByteArrayToStringConverter implements Converter<byte[], String>
{
    INSTANCE;


    @Override
    public String convert(byte[] source) {
        if (source == null) {
            return null;
        }
        return new String(source);
    }
}
