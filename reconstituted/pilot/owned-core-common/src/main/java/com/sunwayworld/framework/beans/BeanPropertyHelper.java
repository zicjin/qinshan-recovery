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

public abstract class BeanPropertyHelper {
   private static final Map<Class<?>, List<BeanPropertyDescriptor>> TYPE_CACHE = new ConcurrentHashMap<>();
   private static final Map<String, BeanPropertyDescriptor> PROPERTY_CACHE = new ConcurrentHashMap<>();

   public static final <T> T getPropertyListenerProxy(T bean, boolean nullPropertyUndefined) {
      BeanPropertyInterceptor<T> interceptor = new BeanPropertyInterceptorImpl<>(bean, nullPropertyUndefined);
      T originalValue = interceptor.getOriginalValue();
      if (ClassUtils.isCglibProxyClass(originalValue.getClass())) {
         return originalValue;
      } else {
         Enhancer enhancer = new Enhancer();
         enhancer.setSuperclass(originalValue.getClass());
         enhancer.setInterfaces(new Class[]{BeanPropertyListener.class});
         enhancer.setCallback(interceptor);
         return (T)enhancer.create();
      }
   }

   public static final List<BeanPropertyDescriptor> getBeanPropertyDescriptorList(Class<?> type) {
      type = ClassUtils.getRawType(type);
      List<BeanPropertyDescriptor> propertyDescriptorList = TYPE_CACHE.get(type);
      if (propertyDescriptorList == null) {
         synchronized (TYPE_CACHE) {
            propertyDescriptorList = TYPE_CACHE.get(type);
            if (propertyDescriptorList == null) {
               List<BeanPropertyDescriptor> var11 = new ArrayList();

               for (Class<?> searchType = type; Object.class != searchType && searchType != null; searchType = searchType.getSuperclass()) {
                  for (Field f : searchType.getDeclaredFields()) {
                     if (!Modifier.isStatic(f.getModifiers()) && !var11.stream().anyMatch(p -> p.getName().equals(f.getName()))) {
                        var11.add(BeanPropertyDescriptor.of(f));
                     }
                  }
               }

               propertyDescriptorList = Collections.unmodifiableList(var11);
               TYPE_CACHE.put(type, propertyDescriptorList);
            }
         }
      }

      return propertyDescriptorList;
   }

   public static final BeanPropertyDescriptor getBeanPropertyDescriptor(Class<?> type, String name) {
      Class<?> rawType = ClassUtils.getRawType(type);
      String key = rawType.getName() + ":" + name.toUpperCase();
      if (!PROPERTY_CACHE.containsKey(key)) {
         synchronized (PROPERTY_CACHE) {
            if (!PROPERTY_CACHE.containsKey(key)) {
               BeanPropertyDescriptor propertyDescriptor = null;

               for (BeanPropertyDescriptor loopPropertyDescriptor : getBeanPropertyDescriptorList(rawType)) {
                  if (loopPropertyDescriptor.getName().equalsIgnoreCase(name)) {
                     propertyDescriptor = loopPropertyDescriptor;
                     break;
                  }
               }

               if (propertyDescriptor != null) {
                  PROPERTY_CACHE.put(key, propertyDescriptor);
               }
            }
         }
      }

      return PROPERTY_CACHE.get(key);
   }

   public static final boolean isValidPropertyName(Class<?> beanType, String propertyName) {
      return getBeanPropertyDescriptorList(beanType).parallelStream().anyMatch(p -> p.getName().equals(propertyName));
   }
}
