/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.support.domain;

import com.sunwayworld.framework.support.domain.Persistable;
import java.io.Serializable;
import java.time.LocalDateTime;

public interface Insertable<ID extends Serializable>
extends Persistable<ID> {
    public String getCreatedById();

    public void setCreatedById(String var1);

    public String getCreatedByName();

    public void setCreatedByName(String var1);

    public LocalDateTime getCreatedTime();

    public void setCreatedTime(LocalDateTime var1);

    public String getCreatedByOrgId();

    public void setCreatedByOrgId(String var1);

    public String getCreatedByOrgName();

    public void setCreatedByOrgName(String var1);
}
