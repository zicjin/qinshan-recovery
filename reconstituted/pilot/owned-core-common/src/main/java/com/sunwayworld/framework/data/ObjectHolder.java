/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.data;

public class ObjectHolder {
    private Object object;

    public ObjectHolder(Object object) {
        this.object = object;
    }

    public ObjectHolder() {
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public <T> T get() {
        return (T)this.object;
    }

    public <T> T getOrDefault(T defaultValue) {
        if (this.object == null) {
            return defaultValue;
        }
        return (T)this.object;
    }
}
