/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.beans;

import com.sunwayworld.framework.beans.BeanPropertyDescriptor;
import java.util.EventObject;

public class BeanPropertyEvent
extends EventObject {
    private static final long serialVersionUID = -1186425809464227870L;
    private BeanPropertyDescriptor propertyDescriptor;
    private String name;
    private Object oldValue;
    private Object newValue;

    public BeanPropertyEvent(Object source, BeanPropertyDescriptor propertyDescriptor) {
        super(source);
        this.propertyDescriptor = propertyDescriptor;
    }

    public BeanPropertyDescriptor getPropertyDescriptor() {
        return this.propertyDescriptor;
    }

    public Object getOldValue() {
        return this.oldValue;
    }

    public Object getNewValue() {
        return this.newValue;
    }

    public void setOldValue(Object oldValue) {
        this.oldValue = oldValue;
    }

    public void setNewValue(Object newValue) {
        this.newValue = newValue;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
