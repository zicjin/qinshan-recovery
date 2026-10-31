package com.sunwayworld.framework.beans;

import com.sunwayworld.framework.beans.impl.BeanPropertyInterceptorImpl;
import com.sunwayworld.framework.utils.ClassUtils;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.sf.cglib.proxy.Enhancer;

/** Owned bean property descriptor and proxy factory. */
@SuppressWarnings("unchecked")
public abstract class BeanPropertyHelper {
    private static final Map<Class<?>, List<BeanPropertyDescriptor>> TYPE_CACHE = new ConcurrentHashMap<Class<?>, List<BeanPropertyDescriptor>>();
    private static final Map<String, BeanPropertyDescriptor> PROPERTY_CACHE = new ConcurrentHashMap<String, BeanPropertyDescriptor>();
    public static final <T> T getPropertyListenerProxy(T bean, boolean nullPropertyUndefined) {
        BeanPropertyInterceptorImpl<T> interceptor = new BeanPropertyInterceptorImpl<T>(bean, nullPropertyUndefined);
        Object originalValue = interceptor.getOriginalValue();
        if (ClassUtils.isCglibProxyClass(originalValue.getClass())) return (T) originalValue;
        Enhancer enhancer = new Enhancer(); enhancer.setSuperclass(originalValue.getClass()); enhancer.setInterfaces(new Class[]{BeanPropertyListener.class}); enhancer.setCallback(interceptor);
        return (T) enhancer.create();
    }
    public static final List<BeanPropertyDescriptor> getBeanPropertyDescriptorList(Class<?> type) {
        Class<?> rawType = ClassUtils.getRawType(type); List<BeanPropertyDescriptor> descriptors = TYPE_CACHE.get(rawType);
        if (descriptors != null) return descriptors;
        synchronized (TYPE_CACHE) {
            descriptors = TYPE_CACHE.get(rawType); if (descriptors != null) return descriptors;
            List<BeanPropertyDescriptor> values = new ArrayList<BeanPropertyDescriptor>();
            for (Class<?> searchType = rawType; searchType != null && searchType != Object.class; searchType = searchType.getSuperclass()) {
                for (Field field : searchType.getDeclaredFields()) if (!Modifier.isStatic(field.getModifiers()) && values.stream().noneMatch(p -> p.getName().equals(field.getName()))) values.add(BeanPropertyDescriptor.of(field));
            }
            descriptors = Collections.unmodifiableList(values); TYPE_CACHE.put(rawType, descriptors); return descriptors;
        }
    }
    public static final BeanPropertyDescriptor getBeanPropertyDescriptor(Class<?> type, String name) {
        Class<?> rawType = ClassUtils.getRawType(type); String key = rawType.getName() + ":" + name.toUpperCase();
        BeanPropertyDescriptor cached = PROPERTY_CACHE.get(key); if (cached != null) return cached;
        synchronized (PROPERTY_CACHE) {
            cached = PROPERTY_CACHE.get(key); if (cached != null) return cached;
            for (BeanPropertyDescriptor descriptor : getBeanPropertyDescriptorList(rawType)) if (descriptor.getName().equalsIgnoreCase(name)) { PROPERTY_CACHE.put(key, descriptor); return descriptor; }
            return null;
        }
    }
    public static final boolean isValidPropertyName(Class<?> beanType, String propertyName) { return getBeanPropertyDescriptorList(beanType).parallelStream().anyMatch(p -> p.getName().equals(propertyName)); }
}
