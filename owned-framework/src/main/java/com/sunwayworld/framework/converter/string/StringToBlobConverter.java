package com.sunwayworld.framework.converter.string;
import com.sunwayworld.framework.converter.Converter;import com.sunwayworld.framework.converter.exception.ConvertFailedException;import java.nio.charset.StandardCharsets;import java.sql.Blob;import javax.sql.rowset.serial.SerialBlob;
public enum StringToBlobConverter implements Converter<String,Blob>{INSTANCE;public Blob convert(String v){if(v==null)return null;try{return new SerialBlob(v.getBytes(StandardCharsets.UTF_8));}catch(Exception e){throw new ConvertFailedException(getSourceType(),getTargetType(),e);}}}
