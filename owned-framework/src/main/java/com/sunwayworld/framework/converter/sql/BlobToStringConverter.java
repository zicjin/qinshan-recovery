package com.sunwayworld.framework.converter.sql;
import com.sunwayworld.framework.converter.Converter;import com.sunwayworld.framework.converter.exception.ConvertFailedException;import java.nio.charset.StandardCharsets;import java.sql.Blob;
public enum BlobToStringConverter implements Converter<Blob,String>{INSTANCE;public String convert(Blob b){if(b==null)return null;try{return new String(b.getBytes(1,(int)b.length()),StandardCharsets.UTF_8);}catch(Exception e){throw new ConvertFailedException(getSourceType(),getTargetType(),e);}}}
