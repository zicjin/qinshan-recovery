/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.support.domain;

import com.sunwayworld.framework.support.domain.Persistable;
import java.io.Serializable;

public interface Orderable<ID extends Serializable>
extends Persistable<ID> {
    public void setOrderNo(Long var1);

    public Long getOrderNo();
}
