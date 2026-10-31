package com.sunwayworld.framework.converter;
public class IdentityConverter<T,S extends T> implements Converter<S,T>{public IdentityConverter(){} public T convert(S value){return value;}}
