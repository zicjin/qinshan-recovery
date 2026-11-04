/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.support.domain;

import com.sunwayworld.framework.support.domain.BaseData;

public interface Persistable<ID>
extends BaseData {
    public ID getId();

    public void setId(ID var1);
}
