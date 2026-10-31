package com.sunwayworld.framework.converter.collection;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sunwayworld.framework.converter.Converter;
import java.util.Map;

public enum MapToJSONObjectConverter implements Converter<Map, JSONObject> {
    INSTANCE;
    public Class<Map> getSourceType() { return Map.class; }
    public Class<JSONObject> getTargetType() { return JSONObject.class; }
    public JSONObject convert(Map source) { return JSONObject.parseObject(JSON.toJSONString(source)); }
}
