/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.converter.collection;

import com.alibaba.fastjson.JSONObject;
import com.sunwayworld.framework.converter.Converter;
import java.util.Map;

public enum MapToStringConverter implements Converter<Map, String>
{
    INSTANCE;


    @Override
    public Class<Map> getSourceType() {
        return Map.class;
    }

    @Override
    public Class<String> getTargetType() {
        return String.class;
    }

    @Override
    public String convert(Map source) {
        return JSONObject.toJSONString(source);
    }
}
