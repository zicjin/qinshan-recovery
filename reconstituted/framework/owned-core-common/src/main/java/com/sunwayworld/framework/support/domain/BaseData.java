/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.support.domain;

import java.io.Serializable;
import java.util.Map;

public interface BaseData
extends Serializable {
    public Map<String, String> getExt$();

    public void setExt$(Map<String, String> var1);

    public void setExt$Item(String var1, String var2);
}
