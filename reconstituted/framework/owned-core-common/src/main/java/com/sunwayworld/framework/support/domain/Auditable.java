/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.support.domain;

import com.sunwayworld.framework.support.domain.Persistable;
import java.io.Serializable;

public interface Auditable<ID extends Serializable>
extends Persistable<ID> {
    public String getProcessStatus();

    public void setProcessStatus(String var1);

    default public Long getVersion() {
        return null;
    }

    default public void setVersion(Long version) {
    }
}
