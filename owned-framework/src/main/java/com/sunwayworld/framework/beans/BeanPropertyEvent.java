package com.sunwayworld.framework.beans;

import java.util.EventObject;

/** Property-change event used by the owned bean property seam. */
public class BeanPropertyEvent extends EventObject {
    private static final long serialVersionUID = -1186425809464227870L;
    private BeanPropertyDescriptor propertyDescriptor;
    private String name;
    private Object oldValue;
    private Object newValue;
    public BeanPropertyEvent(Object source, BeanPropertyDescriptor propertyDescriptor) { super(source); this.propertyDescriptor = propertyDescriptor; }
    public BeanPropertyDescriptor getPropertyDescriptor() { return propertyDescriptor; }
    public Object getOldValue() { return oldValue; }
    public Object getNewValue() { return newValue; }
    public void setOldValue(Object oldValue) { this.oldValue = oldValue; }
    public void setNewValue(Object newValue) { this.newValue = newValue; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
