package com.sunwayworld.framework.beans.impl;

import com.sunwayworld.framework.beans.BeanPropertyDescriptor;
import com.sunwayworld.framework.beans.BeanPropertyEvent;
import com.sunwayworld.framework.beans.BeanPropertyHelper;
import com.sunwayworld.framework.beans.BeanPropertyInterceptor;
import com.sunwayworld.framework.support.domain.BaseData;
import com.sunwayworld.framework.utils.CollectionUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.ReflectionUtils;
import com.sunwayworld.framework.utils.StringUtils;
import java.beans.Transient;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.sf.cglib.proxy.MethodProxy;

public class BeanPropertyInterceptorImpl<T> implements BeanPropertyInterceptor<T> {
   private T bean;
   private boolean nullPropertyUnassigned = false;
   protected List<BeanPropertyEvent> changedPropertyEventList = new ArrayList<>();
   private List<BeanPropertyDescriptor> propertyDescriptorList = new ArrayList<>();

   public BeanPropertyInterceptorImpl(T bean, boolean nullPropertyUndefined) {
      this.bean = bean;
      this.nullPropertyUnassigned = nullPropertyUndefined;
      this.propertyDescriptorList.addAll(BeanPropertyHelper.getBeanPropertyDescriptorList(bean.getClass()));
   }

   @Override
   public Object intercept(Object target, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
      String methodName = method.getName();
      if ("setExt$".equals(methodName)) {
         Map<String, String> oldMap = Optional.of(((BaseData)this.getOriginalValue()).getExt$()).orElseGet(() -> CollectionUtils.emptyMap());
         Map<String, String> newMap = (Map<String, String>)Optional.of((Map)args[0]).orElseGet(() -> CollectionUtils.emptyMap());
         BeanPropertyDescriptor extPd = BeanPropertyDescriptor.of(this.bean.getClass(), "ext$");
         newMap.forEach((k, v) -> {
            String oldValuex = oldMap.get(k);
            if (!oldMap.containsKey(k) || !ObjectUtils.equals(oldValuex, v)) {
               BeanPropertyEvent eventx = new BeanPropertyEvent(target, extPd);
               eventx.setName(k.toUpperCase());
               eventx.setNewValue(v);
               eventx.setOldValue(oldValuex);
               this.changedPropertyEventList.removeIf(e -> k.equalsIgnoreCase(e.getName()));
               this.changedPropertyEventList.add(eventx);
            }
         });
         oldMap.forEach((k, v) -> {
            if (!newMap.containsKey(k)) {
               BeanPropertyEvent eventx = new BeanPropertyEvent(target, extPd);
               eventx.setName(k.toUpperCase());
               eventx.setNewValue(null);
               eventx.setOldValue(v);
               this.changedPropertyEventList.removeIf(e -> k.equalsIgnoreCase(e.getName()));
               this.changedPropertyEventList.add(eventx);
            }
         });
      } else if ("setExt$Item".equals(methodName)) {
         String name = ((String)args[0]).toUpperCase();
         String oldValue = ((BaseData)this.getOriginalValue()).getExt$().get(name);
         if (ObjectUtils.isEmpty(args[1]) || !ObjectUtils.equals(args[1], oldValue)) {
            BeanPropertyEvent event = new BeanPropertyEvent(target, BeanPropertyDescriptor.of(this.bean.getClass(), "ext$"));
            event.setName(name);
            event.setNewValue(args[1]);
            event.setOldValue(oldValue);
            this.changedPropertyEventList.removeIf(e -> name.equalsIgnoreCase(e.getName()));
            this.changedPropertyEventList.add(event);
         }
      } else if (ReflectionUtils.isWriteMethod(method)) {
         String propertyName = StringUtils.uncapitalize(method.getName().substring(3));
         BeanPropertyDescriptor propertyDescriptor = BeanPropertyDescriptor.of(this.bean.getClass(), propertyName);
         if (propertyDescriptor != null && propertyDescriptor.isValidBeanProperty() && !propertyDescriptor.isAnnotationPresent(Transient.class)) {
            Method readMethod = propertyDescriptor.getReadMethod();
            Object oldValue = ReflectionUtils.invokeMethod(readMethod, this.getOriginalValue());
            if (this.isPropertyChanged(oldValue, args[0])) {
               BeanPropertyEvent event = new BeanPropertyEvent(target, propertyDescriptor);
               event.setNewValue(args[0]);
               event.setOldValue(oldValue);
               this.changedPropertyEventList.removeIf(e -> propertyDescriptor.equals(e.getPropertyDescriptor()));
               this.changedPropertyEventList.add(event);
            }
         }
      }

      if ("getChangedPropertyEventList".equals(method.getName())) {
         return this.changedPropertyEventList;
      } else if ("finalize".equals(method.getName())) {
         this.bean = null;
         this.changedPropertyEventList = null;
         return methodProxy.invokeSuper(target, args);
      } else {
         return ReflectionUtils.invokeMethod(method, this.getOriginalValue(), args);
      }
   }

   @Override
   public T getOriginalValue() {
      return this.bean;
   }

   @Override
   public List<BeanPropertyDescriptor> getPropertyDescriptorList() {
      return this.propertyDescriptorList;
   }

   @Override
   public boolean isNullPropertyUnassigned() {
      return this.nullPropertyUnassigned;
   }

   private boolean isPropertyChanged(Object oldValue, Object newValue) {
      return oldValue == null && this.isNullPropertyUnassigned() ? true : !ObjectUtils.equals(oldValue, newValue);
   }
}
