/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.converter.array;

import com.sunwayworld.framework.converter.Converter;
import com.sunwayworld.framework.exception.TypeMismatchException;
import com.sunwayworld.framework.utils.ArrayUtils;
import com.sunwayworld.framework.utils.ClassUtils;
import java.lang.reflect.Array;

public interface WrapperToPrimitiveArrayConverter<S, T>
extends Converter<S, T> {
    @Override
    default public T convert(S source) {
        if (source == null) {
            return null;
        }
        Class<?> wrapperType = ArrayUtils.getArrayType(this.getSourceType());
        Class<?> primitiveType = ArrayUtils.getArrayType(this.getTargetType());
        if (ClassUtils.getWrapperType(primitiveType) != wrapperType) {
            throw new TypeMismatchException(wrapperType, ClassUtils.getWrapperType(primitiveType));
        }
        int size = Array.getLength(source);
        Object result = Array.newInstance(wrapperType, size);
        for (int i = 0; i < size; ++i) {
            Array.set(result, i, Array.get(source, i));
        }
        return (T)result;
    }

    public static enum WrapperToPrimitiveShortArrayConverter implements WrapperToPrimitiveArrayConverter<Short[], short[]>
    {
        INSTANCE;

    }

    public static enum WrapperToPrimitiveLongArrayConverter implements WrapperToPrimitiveArrayConverter<Long[], long[]>
    {
        INSTANCE;

    }

    public static enum WrapperToPrimitiveIntegerArrayConverter implements WrapperToPrimitiveArrayConverter<Integer[], int[]>
    {
        INSTANCE;

    }

    public static enum WrapperToPrimitiveFloatArrayConverter implements WrapperToPrimitiveArrayConverter<Float[], float[]>
    {
        INSTANCE;

    }

    public static enum WrapperToPrimitiveDoubleArrayConverter implements WrapperToPrimitiveArrayConverter<Double[], double[]>
    {
        INSTANCE;

    }

    public static enum WrapperToPrimitiveCharacterArrayConverter implements WrapperToPrimitiveArrayConverter<Character[], char[]>
    {
        INSTANCE;

    }

    public static enum WrapperToPrimitiveByteArrayConverter implements WrapperToPrimitiveArrayConverter<Byte[], byte[]>
    {
        INSTANCE;

    }

    public static enum WrapperToPrimitiveBooleanArrayConverter implements WrapperToPrimitiveArrayConverter<Boolean[], boolean[]>
    {
        INSTANCE;

    }
}
