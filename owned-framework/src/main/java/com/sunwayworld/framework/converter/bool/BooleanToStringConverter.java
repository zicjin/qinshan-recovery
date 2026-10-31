package com.sunwayworld.framework.converter.bool;
import com.sunwayworld.framework.converter.Converter;
public enum BooleanToStringConverter implements Converter<Boolean,String>{INSTANCE;public String convert(Boolean v){return v==null?null:v.toString();}}
