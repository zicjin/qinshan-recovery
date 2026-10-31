package com.sunwayworld.framework.utils;

import com.alibaba.fastjson.JSON;
import com.sunwayworld.framework.exception.EmptyArrayException;
import com.sunwayworld.framework.exception.OutOfRangeException;
import com.sunwayworld.framework.exception.TypeMismatchException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class ArrayUtils {
    public static final Class<?> getArrayType(Class<?> type) {
        if (!type.isArray()) throw new TypeMismatchException("array", type);
        Class<?> component = type.getComponentType();
        while (component.isArray()) component = component.getComponentType();
        return component;
    }
    public static final boolean hasElement(Object value) { return value != null && value.getClass().isArray() && Array.getLength(value) > 0; }
    public static final <T> boolean contains(T[] values, T value) {
        if (values == null || values.length == 0) return false;
        for (T item : values) if (item == null ? value == null : item.equals(value)) return true;
        return false;
    }
    public static final boolean containsIgnoreCase(String[] values, String value) {
        if (values == null || values.length == 0) return false;
        for (String item : values) if (item == null ? value == null : item.equalsIgnoreCase(value)) return true;
        return false;
    }
    @SuppressWarnings("unchecked") public static final <T> T[] concat(T[] left, T[] right) {
        if (left == null) return right; if (right == null) return left;
        T[] out = (T[]) Array.newInstance(left.getClass().getComponentType(), left.length + right.length);
        System.arraycopy(left, 0, out, 0, left.length); System.arraycopy(right, 0, out, left.length, right.length); return out;
    }
    @SuppressWarnings("unchecked") public static final <T> T[] append(T[] values, T value) {
        if (value == null) return values;
        int length = values == null ? 0 : values.length;
        T[] out = (T[]) Array.newInstance(value.getClass(), length + 1);
        if (length > 0) System.arraycopy(values, 0, out, 0, length); out[length] = value; return out;
    }
    public static <T> String arrayToDelimitedString(T[] values, String delimiter) {
        if (!hasElement(values)) return "";
        if (values.length == 1) return ConvertUtils.convert(values[0], String.class, "");
        StringBuilder out = new StringBuilder(); for (T value : values) out.append(ConvertUtils.convert(value, String.class, "")).append(delimiter);
        return out.substring(0, out.length() - delimiter.length());
    }
    public static <T> T[] requireNotEmpty(T[] values) { if (!hasElement(values)) throw new EmptyArrayException(); return values; }
    @SuppressWarnings("unchecked") public static <T> T[] emptyArray(Class<T> type) { return newInstance(type, 0); }
    @SuppressWarnings("unchecked") public static <T> T[] newInstance(Class<T> type, int length) { return (T[]) Array.newInstance(type, length); }
    @SuppressWarnings("unchecked") public static <T> T[] instanceToArray(T value) { T[] out = (T[]) Array.newInstance(value.getClass(), 1); out[0] = value; return out; }
    public static <A,T> T getUniqueTypeValue(A[] values, Class<T> type) {
        if (values == null || values.length == 0) return null; List<T> matches = new ArrayList<T>();
        for (A value : values) if (value != null && value.getClass().equals(type)) matches.add((T) value);
        if (matches.size() > 1) throw new OutOfRangeException(JSON.toJSONString(values) + " contains more than one instance of type" + type + ".");
        return matches.isEmpty() ? null : matches.get(0);
    }
    public static <T> Class<? extends T> getUniqueAssignableType(Class<?>[] values, Class<T> type) {
        if (values == null || values.length == 0) return null; List<Class<?>> matches = new ArrayList<Class<?>>();
        for (Class<?> value : values) if (value != null && type.isAssignableFrom(value)) matches.add(value);
        if (matches.size() > 1) throw new OutOfRangeException(JSON.toJSONString(values) + " contains more than one class assignable type" + type + ".");
        return matches.isEmpty() ? null : (Class<? extends T>) matches.get(0);
    }
    public static <T> boolean isEquals(T[] left, T[] right) {
        if (left == null && right == null) return true; if (left == null || right == null || left.length != right.length) return false;
        for (int i=0;i<left.length;i++) if (left[i] == null ? right[i] != null : !left[i].equals(right[i])) return false; return true;
    }
    public static <T> List<T> asList(T... values) { if (values == null) return CollectionUtils.emptyList(); List<T> out = new ArrayList<T>(); for (T value : values) out.add(value); return out; }
    public static <T> boolean isEmpty(T... values) { return values == null || values.length == 0; }
    @SuppressWarnings("unchecked") public static <T> T[] subarray(T[] values, int start, int length) {
        if (!hasElement(values)) return values; Class<?> type = values[0].getClass(); if (values.length < start + length) return emptyArray((Class<T>) type);
        T[] out = newInstance((Class<T>) type, length); System.arraycopy(values, start, out, 0, length); return out;
    }
    public static int[] subarray(int[] values,int start,int length) { if (values==null || values.length==0) return values; if(values.length<start+length)return new int[0]; int[] out=new int[length];System.arraycopy(values,start,out,0,length);return out; }
    public static long[] subarray(long[] values,int start,int length) { if (values==null || values.length==0) return values; if(values.length<start+length)return new long[0]; long[] out=new long[length];System.arraycopy(values,start,out,0,length);return out; }
    public static double[] subarray(double[] values,int start,int length) { if (values==null || values.length==0) return values; if(values.length<start+length)return new double[0]; double[] out=new double[length];System.arraycopy(values,start,out,0,length);return out; }
    public static byte[] subarray(byte[] values,int start,int length) { if (values==null || values.length==0) return values; if(values.length<start+length)return new byte[0]; byte[] out=new byte[length];System.arraycopy(values,start,out,0,length);return out; }
    public static final int[] concat(int[] left,int[] right){if(left==null)return right;if(right==null)return left;int[]out=new int[left.length+right.length];System.arraycopy(left,0,out,0,left.length);System.arraycopy(right,0,out,left.length,right.length);return out;}
    public static final long[] concat(long[] left,long[] right){if(left==null)return right;if(right==null)return left;long[]out=new long[left.length+right.length];System.arraycopy(left,0,out,0,left.length);System.arraycopy(right,0,out,left.length,right.length);return out;}
    public static final double[] concat(double[] left,double[] right){if(left==null)return right;if(right==null)return left;double[]out=new double[left.length+right.length];System.arraycopy(left,0,out,0,left.length);System.arraycopy(right,0,out,left.length,right.length);return out;}
    public static final byte[] concat(byte[] left,byte[] right){if(left==null)return right;if(right==null)return left;byte[]out=new byte[left.length+right.length];System.arraycopy(left,0,out,0,left.length);System.arraycopy(right,0,out,left.length,right.length);return out;}
}
