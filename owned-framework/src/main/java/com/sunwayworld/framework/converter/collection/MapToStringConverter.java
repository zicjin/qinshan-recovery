package com.sunwayworld.framework.converter.collection;

import com.alibaba.fastjson.JSONObject;
import com.sunwayworld.framework.converter.Converter;
import java.util.Map;

public enum MapToStringConverter implements Converter<Map, String> {
    INSTANCE;
    public Class<Map> getSourceType() { return Map.class; }
    public Class<String> getTargetType() { return String.class; }
    public String convert(Map source) { return JSONObject.toJSONString(source); }
}
