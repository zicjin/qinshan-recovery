package com.sunwayworld.framework.beans;

import java.util.List;
import net.sf.cglib.proxy.MethodInterceptor;

/** Interceptor contract exposed by bean property proxies. */
public interface BeanPropertyInterceptor<T> extends MethodInterceptor {
    T getOriginalValue();
    List<BeanPropertyDescriptor> getPropertyDescriptorList();
    boolean isNullPropertyUnassigned();
}
