package com.sunwayworld.framework.converter.string;
import com.sunwayworld.framework.converter.Converter;import com.sunwayworld.framework.converter.exception.ConvertFailedException;import java.io.*;import java.nio.charset.StandardCharsets;import java.util.Properties;
public enum StringToPropertiesConverter implements Converter<String,Properties>{INSTANCE;public Properties convert(String v){try{Properties p=new Properties();p.load(new ByteArrayInputStream(v.getBytes(StandardCharsets.UTF_8)));return p;}catch(IOException e){throw new ConvertFailedException(getSourceType(),getTargetType(),e);}}}
