package com.sunwayworld.framework.converter.array;
import com.sunwayworld.framework.converter.Converter;import java.lang.reflect.Array;
public interface PrimitiveToWrapperArrayConverter<S,T> extends Converter<S,T>{
 @SuppressWarnings("unchecked") default T convert(S source){
  if(source==null)return null;
  Class<?> wrapper=getTargetType().getComponentType();
  int n=Array.getLength(source);Object out=Array.newInstance(wrapper,n);
  for(int i=0;i<n;i++)Array.set(out,i,Array.get(source,i));return (T)out;
 }

 enum PrimitiveToWrapperBooleanArrayConverter implements PrimitiveToWrapperArrayConverter<boolean[],Boolean[]>{INSTANCE}
 enum PrimitiveToWrapperByteArrayConverter implements PrimitiveToWrapperArrayConverter<byte[],Byte[]>{INSTANCE}
 enum PrimitiveToWrapperCharacterArrayConverter implements PrimitiveToWrapperArrayConverter<char[],Character[]>{INSTANCE}
 enum PrimitiveToWrapperDoubleArrayConverter implements PrimitiveToWrapperArrayConverter<double[],Double[]>{INSTANCE}
 enum PrimitiveToWrapperFloatArrayConverter implements PrimitiveToWrapperArrayConverter<float[],Float[]>{INSTANCE}
 enum PrimitiveToWrapperIntegerArrayConverter implements PrimitiveToWrapperArrayConverter<int[],Integer[]>{INSTANCE}
 enum PrimitiveToWrapperLongArrayConverter implements PrimitiveToWrapperArrayConverter<long[],Long[]>{INSTANCE}
 enum PrimitiveToWrapperShortArrayConverter implements PrimitiveToWrapperArrayConverter<short[],Short[]>{INSTANCE}
}
