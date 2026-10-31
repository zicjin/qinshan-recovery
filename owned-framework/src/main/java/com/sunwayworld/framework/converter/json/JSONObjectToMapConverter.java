package com.sunwayworld.framework.converter.json;

import com.alibaba.fastjson.JSONObject;
import com.sunwayworld.framework.converter.Converter;
import java.util.Map;

public enum JSONObjectToMapConverter implements Converter<JSONObject, Map> {
    INSTANCE;
    public Class<JSONObject> getSourceType() { return JSONObject.class; }
    public Class<Map> getTargetType() { return Map.class; }
    public Map convert(JSONObject source) { return (Map) source.toJavaObject(Map.class); }
}
