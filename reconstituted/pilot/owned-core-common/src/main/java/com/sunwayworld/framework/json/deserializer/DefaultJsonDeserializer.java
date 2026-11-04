package com.sunwayworld.framework.json.deserializer;

import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;
import com.sunwayworld.framework.utils.ConvertUtils;
import java.lang.reflect.Type;

public enum DefaultJsonDeserializer implements ObjectDeserializer {
   INSTANCE;

   @Override
   public <T> T deserialze(DefaultJSONParser paramDefaultJSONParser, Type paramType, Object paramObject) {
      return ConvertUtils.convert(paramDefaultJSONParser.parse(paramObject), (Class<T>)paramType);
   }

   @Override
   public int getFastMatchToken() {
      return 2;
   }
}
