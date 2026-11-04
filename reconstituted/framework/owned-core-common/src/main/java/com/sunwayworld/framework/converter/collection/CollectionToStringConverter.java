/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.converter.collection;

import com.alibaba.fastjson.JSONObject;
import com.sunwayworld.framework.converter.Converter;

public enum CollectionToStringConverter implements Converter<Iterable, String>
{
    INSTANCE;


    @Override
    public Class<Iterable> getSourceType() {
        return Iterable.class;
    }

    @Override
    public Class<String> getTargetType() {
        return String.class;
    }

    @Override
    public String convert(Iterable source) {
        return JSONObject.toJSONString(source);
    }
}
