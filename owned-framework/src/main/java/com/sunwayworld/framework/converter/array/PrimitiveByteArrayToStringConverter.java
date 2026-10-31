package com.sunwayworld.framework.converter.array;
import com.sunwayworld.framework.converter.Converter;import java.nio.charset.StandardCharsets;
public enum PrimitiveByteArrayToStringConverter implements Converter<byte[],String>{INSTANCE;public String convert(byte[] v){return v==null?null:new String(v);}}
