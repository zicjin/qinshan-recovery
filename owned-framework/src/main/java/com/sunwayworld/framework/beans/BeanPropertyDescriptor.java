package com.sunwayworld.framework.beans;

import com.sunwayworld.framework.beans.exception.BeanException;
import com.sunwayworld.framework.cache.memory.MemoryCacheManager;
import com.sunwayworld.framework.exception.ReflectionException;
import com.sunwayworld.framework.utils.ClassUtils;
import com.sunwayworld.framework.utils.ConvertUtils;
import com.sunwayworld.framework.utils.ReflectionUtils;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Optional;

/** Owned property descriptor preserving the supplier's bean property seam. */
public class BeanPropertyDescriptor {
    private Class<?> type;
    private Field property;
    private Method readMethod;
    private Method writeMethod;
    protected BeanPropertyDescriptor() { }

    public static BeanPropertyDescriptor of(Class<?> type, String propertyName) {
        Class<?> rawType = ClassUtils.getRawType(type);
        String key = rawType.getName() + ":" + propertyName;
        BeanPropertyDescriptor cached = MemoryCacheManager.get(key);
        if (cached != null) return cached;
        synchronized (BeanPropertyDescriptor.class) {
            cached = MemoryCacheManager.get(key);
            if (cached != null) return cached;
            BeanPropertyDescriptor descriptor = new BeanPropertyDescriptor();
            descriptor.type = rawType;
            descriptor.property = ReflectionUtils.findField(rawType, propertyName);
            if (descriptor.property == null) return null;
            descriptor.readMethod = Optional.ofNullable(ReflectionUtils.findReadMethod(rawType, propertyName))
                    .orElseThrow(() -> new BeanException(rawType.getName() + " missing get method of " + propertyName));
            descriptor.writeMethod = Optional.ofNullable(ReflectionUtils.findWriteMethod(rawType, propertyName))
                    .orElseThrow(() -> new BeanException(rawType.getName() + " missing set/is method of " + propertyName));
            MemoryCacheManager.put(key, descriptor);
            return descriptor;
        }
    }
    public static BeanPropertyDescriptor of(Field property) { return of(property.getDeclaringClass(), property.getName()); }
    public Class<?> getDeclaringType() { return type; }
    public Field getProperty() { return property; }
    public Method getReadMethod() { return readMethod; }
    public Method getWriteMethod() { return writeMethod; }
    public String getName() { return property.getName(); }
    public Class<?> getPropertyType() { return property.getType(); }
    public boolean isValidBeanProperty() { return readMethod != null && writeMethod != null; }
    public boolean isExt$BeanProperty() { return "ext$".equals(property.getName()); }
    public <T extends Annotation> T getAnnotationIfPresent(Class<T> annotationClass) {
        T annotation = property.getAnnotation(annotationClass);
        if (annotation == null && readMethod != null) annotation = readMethod.getAnnotation(annotationClass);
        if (annotation == null && writeMethod != null) annotation = writeMethod.getAnnotation(annotationClass);
        return annotation;
    }
    public <T extends Annotation> boolean isAnnotationPresent(Class<T> annotationClass) { return annotationClass != null && getAnnotationIfPresent(annotationClass) != null; }
    public <T> void passValue(T source, T target) {
        try { writeMethod.invoke(target, readMethod.invoke(source, new Object[0])); }
        catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) { throw new ReflectionException(e); }
    }
    @SuppressWarnings("unchecked") public <T, V> V getPropertyValue(T item) { return (V) ReflectionUtils.invokeMethod(readMethod, item); }
    public <T, V> void setPropertyValue(T item, V value) { ReflectionUtils.invokeMethod(writeMethod, item, ConvertUtils.convert(value, getPropertyType())); }
    public boolean equals(Object target) {
        if (!(target instanceof BeanPropertyDescriptor)) return false;
        BeanPropertyDescriptor other = (BeanPropertyDescriptor) target;
        return other.getName().equals(getName()) && (getDeclaringType().isAssignableFrom(other.getDeclaringType()) || other.getDeclaringType().isAssignableFrom(getDeclaringType()));
    }
    public int hashCode() { return property == null ? 42 : property.getName().hashCode(); }
}
