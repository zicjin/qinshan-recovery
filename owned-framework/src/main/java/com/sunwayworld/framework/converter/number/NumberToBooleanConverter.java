package com.sunwayworld.framework.converter.number;
import com.sunwayworld.framework.converter.Converter;
public enum NumberToBooleanConverter implements Converter<Number,Boolean>{INSTANCE; public Class<Number> getSourceType(){return Number.class;} public Class<Boolean> getTargetType(){return Boolean.class;} public Boolean convert(Number source){return source!=null&&source.doubleValue()>0.0;}}
