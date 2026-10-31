package com.sunwayworld.framework.data;

/** Mutable holder for an optional object value. */
public class ObjectHolder {
    private Object object;

    public ObjectHolder(Object object) { this.object = object; }
    public ObjectHolder() { }
    public void setObject(Object object) { this.object = object; }
    @SuppressWarnings("unchecked")
    public <T> T get() { return (T) object; }
    @SuppressWarnings("unchecked")
    public <T> T getOrDefault(T defaultValue) { return (T) (object == null ? defaultValue : object); }
}
