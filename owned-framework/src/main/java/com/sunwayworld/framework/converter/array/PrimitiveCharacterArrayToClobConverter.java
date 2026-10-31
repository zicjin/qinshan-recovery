package com.sunwayworld.framework.converter.array;
import com.sunwayworld.framework.converter.Converter;import com.sunwayworld.framework.converter.exception.ConvertFailedException;import java.sql.Clob;import javax.sql.rowset.serial.SerialClob;
public enum PrimitiveCharacterArrayToClobConverter implements Converter<char[],Clob>{INSTANCE;public Clob convert(char[] v){if(v==null)return null;try{return new SerialClob(v);}catch(Exception e){throw new ConvertFailedException(getSourceType(),getTargetType(),e);}}}
