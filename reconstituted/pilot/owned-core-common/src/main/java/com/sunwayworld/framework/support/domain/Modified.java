/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.support.domain;

import com.sunwayworld.framework.support.domain.Persistable;
import java.io.Serializable;

public interface Modified<ID extends Serializable>
extends Persistable<ID> {
    public ID getOldId();

    public void setOldId(ID var1);
}
