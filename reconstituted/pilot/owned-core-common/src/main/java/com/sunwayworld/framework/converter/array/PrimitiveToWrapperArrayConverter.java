/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.converter.array;

import com.sunwayworld.framework.converter.Converter;
import com.sunwayworld.framework.exception.TypeMismatchException;
import com.sunwayworld.framework.utils.ArrayUtils;
import com.sunwayworld.framework.utils.ClassUtils;
import java.lang.reflect.Array;

public interface PrimitiveToWrapperArrayConverter<S, T>
extends Converter<S, T> {
    @Override
    default public T convert(S source) {
        if (source == null) {
            return null;
        }
        Class<?> primitiveType = ArrayUtils.getArrayType(this.getSourceType());
        Class<?> wrapperType = ArrayUtils.getArrayType(this.getTargetType());
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

    public static enum PrimitiveToWrapperLongArrayConverter implements PrimitiveToWrapperArrayConverter<long[], Long[]>
    {
        INSTANCE;

    }

    public static enum PrimitiveToWrapperFloatArrayConverter implements PrimitiveToWrapperArrayConverter<float[], Float[]>
    {
        INSTANCE;

    }

    public static enum PrimitiveToWrapperDoubleArrayConverter implements PrimitiveToWrapperArrayConverter<double[], Double[]>
    {
        INSTANCE;

    }

    public static enum PrimitiveToWrapperIntegerArrayConverter implements PrimitiveToWrapperArrayConverter<int[], Integer[]>
    {
        INSTANCE;

    }

    public static enum PrimitiveToWrapperShortArrayConverter implements PrimitiveToWrapperArrayConverter<short[], Short[]>
    {
        INSTANCE;

    }

    public static enum PrimitiveToWrapperCharacterArrayConverter implements PrimitiveToWrapperArrayConverter<char[], Character[]>
    {
        INSTANCE;

    }

    public static enum PrimitiveToWrapperByteArrayConverter implements PrimitiveToWrapperArrayConverter<byte[], Byte[]>
    {
        INSTANCE;

    }

    public static enum PrimitiveToWrapperBooleanArrayConverter implements PrimitiveToWrapperArrayConverter<boolean[], Boolean[]>
    {
        INSTANCE;

    }
}
