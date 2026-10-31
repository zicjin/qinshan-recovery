package com.sunwayworld.framework.json.deserializer;

import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;
import com.sunwayworld.framework.utils.ConvertUtils;
import java.lang.reflect.Type;

/** Default Fastjson deserializer that delegates target conversion to the owned converter seam. */
public enum DefaultJsonDeserializer implements ObjectDeserializer {
    INSTANCE;

    @SuppressWarnings("unchecked")
    public <T> T deserialze(DefaultJSONParser parser, Type type, Object fieldName) {
        return (T) ConvertUtils.convert(parser.parse(fieldName), (Class<?>) type);
    }

    public int getFastMatchToken() { return 2; }
}
