/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.beans;

import com.sunwayworld.framework.beans.BeanPropertyEvent;
import java.util.List;

public interface BeanPropertyListener {
    public List<BeanPropertyEvent> getChangedPropertyEventList();
}
