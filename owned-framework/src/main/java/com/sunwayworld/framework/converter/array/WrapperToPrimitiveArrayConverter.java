package com.sunwayworld.framework.converter.array;
import com.sunwayworld.framework.converter.Converter;import java.lang.reflect.Array;
public interface WrapperToPrimitiveArrayConverter<S,T> extends Converter<S,T>{
 @SuppressWarnings("unchecked") default T convert(S source){if(source==null)return null;Class<?> c=getSourceType().getComponentType();int n=Array.getLength(source);Object out=Array.newInstance(c,n);for(int i=0;i<n;i++)Array.set(out,i,Array.get(source,i));return (T)out;}
 enum WrapperToPrimitiveBooleanArrayConverter implements WrapperToPrimitiveArrayConverter<Boolean[],boolean[]>{INSTANCE}
 enum WrapperToPrimitiveByteArrayConverter implements WrapperToPrimitiveArrayConverter<Byte[],byte[]>{INSTANCE}
 enum WrapperToPrimitiveCharacterArrayConverter implements WrapperToPrimitiveArrayConverter<Character[],char[]>{INSTANCE}
 enum WrapperToPrimitiveDoubleArrayConverter implements WrapperToPrimitiveArrayConverter<Double[],double[]>{INSTANCE}
 enum WrapperToPrimitiveFloatArrayConverter implements WrapperToPrimitiveArrayConverter<Float[],float[]>{INSTANCE}
 enum WrapperToPrimitiveIntegerArrayConverter implements WrapperToPrimitiveArrayConverter<Integer[],int[]>{INSTANCE}
 enum WrapperToPrimitiveLongArrayConverter implements WrapperToPrimitiveArrayConverter<Long[],long[]>{INSTANCE}
 enum WrapperToPrimitiveShortArrayConverter implements WrapperToPrimitiveArrayConverter<Short[],short[]>{INSTANCE}
}
