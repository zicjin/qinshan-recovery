/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.converter.json;

import com.alibaba.fastjson.JSONObject;
import com.sunwayworld.framework.converter.Converter;
import java.util.Map;

public enum JSONObjectToMapConverter implements Converter<JSONObject, Map>
{
    INSTANCE;


    @Override
    public Class<JSONObject> getSourceType() {
        return JSONObject.class;
    }

    @Override
    public Class<Map> getTargetType() {
        return Map.class;
    }

    @Override
    public Map convert(JSONObject source) {
        return source.toJavaObject(Map.class);
    }
}
