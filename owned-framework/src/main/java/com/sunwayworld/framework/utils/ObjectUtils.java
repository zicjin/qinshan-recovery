package com.sunwayworld.framework.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializeFilter;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.ValueFilter;
import com.sunwayworld.framework.exception.CloneException;
import com.sunwayworld.framework.exception.ClassInstantiationException;
import com.sunwayworld.framework.beans.exception.BeanException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Serializable;
import java.io.StringWriter;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.charset.Charset;
import java.sql.Blob;
import java.sql.Clob;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpSession;

/** Small, dependency-closed utility contract used by mapper/filter seams. */
public abstract class ObjectUtils {
    private static ValueFilter valueFilter = new ValueFilter() {
        @Override public Object process(Object object, String name, Object value) {
            if (value == null) return null;
            Class<?> type = value.getClass();
            if (byte[].class.isAssignableFrom(type)) return "-bytes-";
            if (Byte[].class.isAssignableFrom(type)) return "-Bytes-";
            if (InputStream.class.isAssignableFrom(type)) return "-InputStream-";
            if (OutputStream.class.isAssignableFrom(type)) return "-OutputStream-";
            if (value instanceof CharSequence && ((CharSequence) value).length() > 124) return value.toString().substring(0, 123) + "...(more)";
            return value;
        }
    };

    public static final boolean equals(Object left, Object right) {
        if (left == null && (right == null || "".equals(right))) return true;
        if (right == null && "".equals(left)) return true;
        if (left == null || right == null || !left.getClass().equals(right.getClass())) return false;
        if (left instanceof Collection<?>) {
            Collection<?> a=(Collection<?>)left,b=(Collection<?>)right;
            for (Object value:a) if (!b.contains(value)) return false;
            for (Object value:b) if (!a.contains(value)) return false;
            return true;
        }
        if (left instanceof Map<?,?>) {
            Map<?,?> a=(Map<?,?>)left,b=(Map<?,?>)right;
            for (Map.Entry<?,?> ea:a.entrySet()) { boolean found=false; for(Map.Entry<?,?> eb:b.entrySet()) if(eb.getKey().equals(ea.getKey())&&equals(eb.getValue(),ea.getValue())) {found=true;break;} if(!found)return false; }
            for (Map.Entry<?,?> eb:b.entrySet()) { boolean found=false; for(Map.Entry<?,?> ea:a.entrySet()) if(ea.getKey().equals(eb.getKey())&&equals(ea.getValue(),eb.getValue())) {found=true;break;} if(!found)return false; }
            return true;
        }
        return left.equals(right);
    }

    public static final String toShortString(Object target) {
        if (target == null) return "";
        Class<?> type=target.getClass();
        if (type.isArray()) { int n=Array.getLength(target); String[] values=new String[n]; for(int i=0;i<n;i++) values[i]=toString(Array.get(target,i)); return Arrays.toString(values); }
        if (target instanceof String) return (String)target;
        if (target instanceof Number) return formatNumber((Number)target);
        if (target instanceof Throwable) return getStackTrace((Throwable)target);
        if (cannotDisplayString(type)) return target.toString();
        try { return JSON.toJSONString(target, (SerializeFilter)valueFilter, new SerializerFeature[0]); }
        catch (Exception ignored) { return target.toString(); }
    }

    public static final boolean isEmpty(Object value) {
        if (value == null || "".equals(value)) return true;
        Class<?> type=value.getClass();
        if (type.isArray()) return Array.getLength(value)==0;
        if (value instanceof Collection<?>) return ((Collection<?>)value).isEmpty();
        if (value instanceof Map<?,?>) return ((Map<?,?>)value).isEmpty();
        return false;
    }

    @SuppressWarnings("unchecked")
    public static final <T> T clone(T target) {
        if (target == null) return null;
        try {
            ByteArrayOutputStream bytes=new ByteArrayOutputStream();
            new ObjectOutputStream(bytes).writeObject(target);
            return (T)new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray())).readObject();
        } catch (java.io.IOException | ClassNotFoundException failure) {
            Class<?> type=target.getClass();
            if (type.isArray()) { int n=Array.getLength(target); Object copy=Array.newInstance(type.getComponentType(),n); for(int i=0;i<n;i++) Array.set(copy,i,cloneBean(Array.get(target,i), type.getComponentType())); return (T)copy; }
            if (target instanceof List<?>) { List<?> source=(List<?>)target; java.util.ArrayList<Object> copy=new java.util.ArrayList<Object>(); for(Object value:source) copy.add(cloneBean(value)); return (T)copy; }
            if (type.getName().startsWith("com.sunwayworld.")) return (T)cloneBean(target);
            throw new CloneException(target, failure);
        }
    }

    public static final <T> String toString(T target) {
        if (target == null) return "";
        if (target instanceof String) return (String)target;
        if (target instanceof Number) return com.sunwayworld.framework.converter.number.NumberToStringConverter.INSTANCE.convert((Number) target);
        if (target instanceof java.sql.Time) return com.sunwayworld.owned.internal.converter.DateTimeState.formatTime((java.sql.Time) target);
        if (target instanceof LocalDate) return com.sunwayworld.framework.converter.date.JSR310ToStringGenericConverter.LocalDateToStringConverter.INSTANCE.convert((LocalDate) target);
        if (target instanceof LocalDateTime) return com.sunwayworld.framework.converter.date.JSR310ToStringGenericConverter.LocalDateTimeToStringConverter.INSTANCE.convert((LocalDateTime) target);
        if (target instanceof LocalTime) return com.sunwayworld.framework.converter.date.JSR310ToStringGenericConverter.LocalTimeToStringConverter.INSTANCE.convert((LocalTime) target);
        if (target instanceof Date) return com.sunwayworld.framework.converter.date.DateToStringConverterFactory.DateToStringConverter.INSTANCE.convert((Date) target);
        if (target instanceof Blob) return com.sunwayworld.framework.converter.sql.BlobToStringConverter.INSTANCE.convert((Blob) target);
        if (target instanceof Clob) return com.sunwayworld.framework.converter.sql.ClobToStringConverter.INSTANCE.convert((Clob) target);
        if (target instanceof byte[]) return new String((byte[])target, java.nio.charset.StandardCharsets.UTF_8);
        if (target instanceof Collection<?> || target instanceof Map<?,?>) return JSON.toJSONString(target);
        if (target instanceof Boolean) return String.valueOf(target);
        if (target instanceof Reader) {
            return com.sunwayworld.framework.converter.reader.ReaderToStringConverter.INSTANCE.convert((Reader) target);
        }
        throw new com.sunwayworld.framework.converter.exception.ConverterNotFoundException(target.getClass(), String.class);
    }

    public static <T> T defaultIfNull(T target, T defaultValue) { return target == null ? defaultValue : target; }

    public static final String getStackTrace(Throwable throwable) {
        StringWriter text=new StringWriter(); PrintWriter writer=new PrintWriter(text); throwable.printStackTrace(writer); return text.toString();
    }

    private static String formatNumber(Number number) {
        if (number == null) return "";
        if (number instanceof Float && Float.isNaN(number.floatValue())) return "";
        if (number instanceof Double && Double.isNaN(number.doubleValue())) return "";
        if (number instanceof Float) return new DecimalFormat("0.##########").format(Double.valueOf(Float.toString(number.floatValue())));
        if (number instanceof Byte || number instanceof Short || number instanceof Integer || number instanceof Long || number instanceof java.math.BigInteger) return new DecimalFormat("0").format(number);
        return new DecimalFormat("0.##########").format(number);
    }
    private static boolean cannotDisplayString(Class<?> type) { return ServletRequest.class.isAssignableFrom(type)||ServletResponse.class.isAssignableFrom(type)||HttpSession.class.isAssignableFrom(type)||AutoCloseable.class.isAssignableFrom(type)||Closeable.class.isAssignableFrom(type)||Blob.class.isAssignableFrom(type)||Clob.class.isAssignableFrom(type)||byte[].class.isAssignableFrom(type)||Byte[].class.isAssignableFrom(type)||Charset.class.isAssignableFrom(type); }
    private static Object cloneBean(Object source) { return source == null ? null : cloneBean(source, source.getClass()); }
    private static Object cloneBean(Object source, Class<?> type) {
        if (source == null) return null;
        Object copy;
        try {
            java.lang.reflect.Constructor<?>[] constructors = type.getDeclaredConstructors();
            if (constructors.length == 0) throw new NoSuchMethodException("no constructor");
            java.lang.reflect.Constructor<?> constructor = constructors[0];
            for (java.lang.reflect.Constructor<?> candidate : constructors)
                if (candidate.getParameterTypes().length < constructor.getParameterTypes().length) constructor = candidate;
            Object[] args = new Object[constructor.getParameterTypes().length];
            copy = constructor.newInstance(args);
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | java.lang.reflect.InvocationTargetException | NoSuchMethodException failure) {
            throw new ClassInstantiationException(type, failure);
        }
        java.util.Set<String> properties = new java.util.HashSet<String>();
        for (Method getter : source.getClass().getMethods()) {
            if (getter.getName().startsWith("get") && getter.getParameterTypes().length == 0 && getter.getName().length() > 3) {
                String property = getter.getName().substring(3); properties.add(property.toLowerCase(java.util.Locale.ROOT));
                try { Method setter = type.getMethod("set" + property, getter.getReturnType()); if (Modifier.isPublic(setter.getModifiers())) setter.invoke(copy, getter.invoke(source)); }
                catch (NoSuchMethodException ignored) { }
                catch (IllegalAccessException | java.lang.reflect.InvocationTargetException failure) { throw new BeanException(type.getName() + " property copy failed for " + property); }
            }
        }
        for (java.lang.reflect.Field field : source.getClass().getDeclaredFields()) {
            if (Modifier.isStatic(field.getModifiers()) || field.isSynthetic()) continue;
            if (!properties.contains(field.getName().toLowerCase(java.util.Locale.ROOT)))
                throw new BeanException(source.getClass().getName() + " missing get method of " + field.getName());
        }
        return copy;
    }
}
