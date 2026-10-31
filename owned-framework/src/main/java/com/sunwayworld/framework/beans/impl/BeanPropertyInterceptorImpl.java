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

/** Owned CGLIB interceptor preserving property-change observation behavior. */
public class BeanPropertyInterceptorImpl<T> implements BeanPropertyInterceptor<T> {
    private T bean;
    private boolean nullPropertyUnassigned;
    protected List<BeanPropertyEvent> changedPropertyEventList = new ArrayList<BeanPropertyEvent>();
    private List<BeanPropertyDescriptor> propertyDescriptorList = new ArrayList<BeanPropertyDescriptor>();
    public BeanPropertyInterceptorImpl(T bean, boolean nullPropertyUndefined) {
        this.bean = bean;
        this.nullPropertyUnassigned = nullPropertyUndefined;
        this.propertyDescriptorList.addAll(BeanPropertyHelper.getBeanPropertyDescriptorList(bean.getClass()));
    }
    @SuppressWarnings({"rawtypes", "unchecked"})
    public Object intercept(Object target, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
        String methodName = method.getName();
        if ("setExt$".equals(methodName) && this.getOriginalValue() instanceof BaseData) {
            Map oldMap = Optional.ofNullable(((BaseData) this.getOriginalValue()).getExt$()).orElseGet(() -> CollectionUtils.emptyMap());
            Map newMap = Optional.ofNullable((Map) args[0]).orElseGet(() -> CollectionUtils.emptyMap());
            BeanPropertyDescriptor extPd = BeanPropertyDescriptor.of(this.bean.getClass(), "ext$");
            newMap.forEach((k, v) -> {
                String oldValue = (String) oldMap.get(k);
                if (!oldMap.containsKey(k) || !ObjectUtils.equals(oldValue, v)) {
                    BeanPropertyEvent event = new BeanPropertyEvent(target, extPd); event.setName(String.valueOf(k).toUpperCase()); event.setNewValue(v); event.setOldValue(oldValue);
                    this.changedPropertyEventList.removeIf(e -> String.valueOf(k).equalsIgnoreCase(e.getName())); this.changedPropertyEventList.add(event);
                }
            });
            oldMap.forEach((k, v) -> { if (!newMap.containsKey(k)) { BeanPropertyEvent event = new BeanPropertyEvent(target, extPd); event.setName(String.valueOf(k).toUpperCase()); event.setNewValue(null); event.setOldValue(v); this.changedPropertyEventList.removeIf(e -> String.valueOf(k).equalsIgnoreCase(e.getName())); this.changedPropertyEventList.add(event); } });
        } else if ("setExt$Item".equals(methodName) && this.getOriginalValue() instanceof BaseData) {
            String name = String.valueOf(args[0]).toUpperCase(); Map<String, String> ext = ((BaseData) this.getOriginalValue()).getExt$(); String oldValue = ext == null ? null : ext.get(name);
            if (ObjectUtils.isEmpty(args[1]) || !ObjectUtils.equals(args[1], oldValue)) {
                BeanPropertyEvent event = new BeanPropertyEvent(target, BeanPropertyDescriptor.of(this.bean.getClass(), "ext$")); event.setName(name); event.setNewValue(args[1]); event.setOldValue(oldValue);
                this.changedPropertyEventList.removeIf(e -> name.equalsIgnoreCase(e.getName())); this.changedPropertyEventList.add(event);
            }
        } else if (ReflectionUtils.isWriteMethod(method)) {
            String propertyName = StringUtils.uncapitalize(method.getName().substring(3));
            BeanPropertyDescriptor descriptor = BeanPropertyDescriptor.of(this.bean.getClass(), propertyName);
            if (descriptor != null && descriptor.isValidBeanProperty() && !descriptor.isAnnotationPresent(Transient.class)) {
                Object oldValue = ReflectionUtils.invokeMethod(descriptor.getReadMethod(), this.getOriginalValue());
                if (isPropertyChanged(oldValue, args == null ? null : args[0])) {
                    BeanPropertyEvent event = new BeanPropertyEvent(target, descriptor); event.setNewValue(args == null ? null : args[0]); event.setOldValue(oldValue);
                    this.changedPropertyEventList.removeIf(e -> descriptor.equals(e.getPropertyDescriptor())); this.changedPropertyEventList.add(event);
                }
            }
        }
        if ("getChangedPropertyEventList".equals(methodName)) return changedPropertyEventList;
        if ("finalize".equals(methodName)) { bean = null; changedPropertyEventList = null; return methodProxy.invokeSuper(target, args); }
        return ReflectionUtils.invokeMethod(method, getOriginalValue(), args == null ? new Object[0] : args);
    }
    public T getOriginalValue() { return bean; }
    public List<BeanPropertyDescriptor> getPropertyDescriptorList() { return propertyDescriptorList; }
    public boolean isNullPropertyUnassigned() { return nullPropertyUnassigned; }
    private boolean isPropertyChanged(Object oldValue, Object newValue) { return oldValue == null && isNullPropertyUnassigned() || !ObjectUtils.equals(oldValue, newValue); }
}
