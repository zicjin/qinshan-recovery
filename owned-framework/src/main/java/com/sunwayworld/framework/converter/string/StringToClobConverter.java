package com.sunwayworld.framework.converter.string;
import com.sunwayworld.framework.converter.Converter;import com.sunwayworld.framework.converter.exception.ConvertFailedException;import java.sql.Clob;import javax.sql.rowset.serial.SerialClob;
public enum StringToClobConverter implements Converter<String,Clob>{INSTANCE;public Clob convert(String v){if(v==null)return null;try{return new SerialClob(v.toCharArray());}catch(Exception e){throw new ConvertFailedException(getSourceType(),getTargetType(),e);}}}
