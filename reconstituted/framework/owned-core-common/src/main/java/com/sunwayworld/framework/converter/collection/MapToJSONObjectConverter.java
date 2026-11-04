/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.converter.collection;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sunwayworld.framework.converter.Converter;
import java.util.Map;

public enum MapToJSONObjectConverter implements Converter<Map, JSONObject>
{
    INSTANCE;


    @Override
    public Class<Map> getSourceType() {
        return Map.class;
    }

    @Override
    public Class<JSONObject> getTargetType() {
        return JSONObject.class;
    }

    @Override
    public JSONObject convert(Map source) {
        return JSONObject.parseObject(JSON.toJSONString(source));
    }
}
