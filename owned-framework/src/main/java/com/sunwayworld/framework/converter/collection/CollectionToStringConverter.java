package com.sunwayworld.framework.converter.collection;

import com.alibaba.fastjson.JSONObject;
import com.sunwayworld.framework.converter.Converter;

public enum CollectionToStringConverter implements Converter<Iterable, String> {
    INSTANCE;
    public Class<Iterable> getSourceType() { return Iterable.class; }
    public Class<String> getTargetType() { return String.class; }
    public String convert(Iterable source) { return JSONObject.toJSONString(source); }
}
