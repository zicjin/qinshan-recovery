/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.support.domain;

import com.sunwayworld.framework.support.domain.Persistable;
import java.io.Serializable;
import java.time.LocalDateTime;

public interface Activatable<ID extends Serializable>
extends Persistable<ID> {
    public String getActivatedFlag();

    public void setActivatedFlag(String var1);

    public String getActivatedById();

    public void setActivatedById(String var1);

    public String getActivatedByName();

    public void setActivatedByName(String var1);

    public LocalDateTime getActivatedTime();

    public void setActivatedTime(LocalDateTime var1);
}
