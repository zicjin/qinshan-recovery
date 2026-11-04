/*
 * Decompiled with CFR 0.152.
 */
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

public class BeanPropertyDescriptor {
    private Class<?> type;
    private Field property;
    private Method readMethod;
    private Method writeMethod;

    protected BeanPropertyDescriptor() {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public static BeanPropertyDescriptor of(Class<?> type, String propertyName) {
        Class<?> rawType = ClassUtils.getRawType(type);
        String key = rawType.getName() + ":" + propertyName;
        BeanPropertyDescriptor propertyDescriptor = (BeanPropertyDescriptor)MemoryCacheManager.get(key);
        if (propertyDescriptor != null) return propertyDescriptor;
        Class<BeanPropertyDescriptor> clazz = BeanPropertyDescriptor.class;
        synchronized (BeanPropertyDescriptor.class) {
            propertyDescriptor = (BeanPropertyDescriptor)MemoryCacheManager.get(key);
            if (propertyDescriptor != null) return propertyDescriptor;
            propertyDescriptor = new BeanPropertyDescriptor();
            propertyDescriptor.type = rawType;
            propertyDescriptor.property = ReflectionUtils.findField(rawType, propertyName);
            if (propertyDescriptor.property == null) {
                // ** MonitorExit[var5_5] (shouldn't be in output)
                return null;
            }
            propertyDescriptor.readMethod = Optional.ofNullable(ReflectionUtils.findReadMethod(rawType, propertyName)).orElseThrow(() -> new BeanException(rawType.getName() + " missing get method of " + propertyName));
            propertyDescriptor.writeMethod = Optional.ofNullable(ReflectionUtils.findWriteMethod(rawType, propertyName)).orElseThrow(() -> new BeanException(rawType.getName() + " missing set/is method of " + propertyName));
            MemoryCacheManager.put(key, propertyDescriptor);
            // ** MonitorExit[var5_5] (shouldn't be in output)
            return propertyDescriptor;
        }
    }

    public static BeanPropertyDescriptor of(Field property) {
        return BeanPropertyDescriptor.of(property.getDeclaringClass(), property.getName());
    }

    public Class<?> getDeclaringType() {
        return this.type;
    }

    public Field getProperty() {
        return this.property;
    }

    public Method getReadMethod() {
        return this.readMethod;
    }

    public Method getWriteMethod() {
        return this.writeMethod;
    }

    public String getName() {
        return this.property.getName();
    }

    public Class<?> getPropertyType() {
        return this.property.getType();
    }

    public boolean isValidBeanProperty() {
        return this.readMethod != null && this.writeMethod != null;
    }

    public boolean isExt$BeanProperty() {
        return "ext$".equals(this.property.getName());
    }

    public <T extends Annotation> T getAnnotationIfPresent(Class<T> annotationClass) {
        T annotation = this.property.getAnnotation(annotationClass);
        if (annotation == null && this.readMethod != null) {
            annotation = this.readMethod.getAnnotation(annotationClass);
        }
        if (annotation == null && this.writeMethod != null) {
            annotation = this.writeMethod.getAnnotation(annotationClass);
        }
        return annotation;
    }

    public <T extends Annotation> boolean isAnnotationPresent(Class<T> annotationClass) {
        if (annotationClass == null) {
            return false;
        }
        return this.getAnnotationIfPresent(annotationClass) != null;
    }

    public <T> void passValue(T source, T target) {
        Method writeMethod = this.getWriteMethod();
        Method readMethod = this.getReadMethod();
        try {
            writeMethod.invoke(target, readMethod.invoke(source, new Object[0]));
        }
        catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new ReflectionException(e);
        }
    }

    public <T, V> V getPropertyValue(T item) {
        return (V)ReflectionUtils.invokeMethod(this.getReadMethod(), item);
    }

    public <T, V> void setPropertyValue(T item, V value) {
        ReflectionUtils.invokeMethod(this.getWriteMethod(), item, ConvertUtils.convert(value, this.getPropertyType()));
    }

    public boolean equals(Object target) {
        if (target == null) {
            return false;
        }
        if (target instanceof BeanPropertyDescriptor) {
            BeanPropertyDescriptor propertyDescriptor = (BeanPropertyDescriptor)target;
            return propertyDescriptor.getName().equals(this.getName()) && (this.getDeclaringType().isAssignableFrom(propertyDescriptor.getDeclaringType()) || propertyDescriptor.getDeclaringType().isAssignableFrom(this.getDeclaringType()));
        }
        return false;
    }

    public int hashCode() {
        if (this.property == null) {
            return 42;
        }
        return this.property.getName().hashCode();
    }
}
