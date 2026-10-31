package com.sunwayworld.framework.converter.array;
import com.sunwayworld.framework.converter.Converter;import com.sunwayworld.framework.converter.exception.ConvertFailedException;import java.sql.Blob;import javax.sql.rowset.serial.SerialBlob;
public enum PrimitiveByteArrayToBlobConverter implements Converter<byte[],Blob>{INSTANCE;public Blob convert(byte[] v){if(v==null)return null;try{return new SerialBlob(v);}catch(Exception e){throw new ConvertFailedException(getSourceType(),getTargetType(),e);}}}
