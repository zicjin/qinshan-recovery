/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.support.domain;

import com.sunwayworld.framework.support.domain.Persistable;
import java.io.Serializable;
import java.time.LocalDateTime;

public interface Suspendable<ID extends Serializable>
extends Persistable<ID> {
    public String getLastSuspendedFlag();

    public void setLastSuspendedFlag(String var1);

    public String getLastSuspendedById();

    public void setLastSuspendedById(String var1);

    public String getLastSuspendedByName();

    public void setLastSuspendedByName(String var1);

    public LocalDateTime getLastSuspendedTime();

    public void setLastSuspendedTime(LocalDateTime var1);
}
