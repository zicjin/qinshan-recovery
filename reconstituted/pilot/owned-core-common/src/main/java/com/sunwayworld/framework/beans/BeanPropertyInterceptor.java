/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.beans;

import com.sunwayworld.framework.beans.BeanPropertyDescriptor;
import java.util.List;
import net.sf.cglib.proxy.MethodInterceptor;

public interface BeanPropertyInterceptor<T>
extends MethodInterceptor {
    public T getOriginalValue();

    public List<BeanPropertyDescriptor> getPropertyDescriptorList();

    public boolean isNullPropertyUnassigned();
}
