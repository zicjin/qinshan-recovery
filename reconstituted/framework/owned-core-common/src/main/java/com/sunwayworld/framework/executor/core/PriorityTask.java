/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.executor.core;

import com.sunwayworld.framework.security.bean.LoginUser;
import java.util.Locale;

public abstract class PriorityTask
implements Comparable<PriorityTask> {
    abstract int getPriorityValue();

    abstract Locale getLocale();

    abstract LoginUser getLoginUser();

    abstract String getTenant();

    @Override
    public final int compareTo(PriorityTask priorityTask) {
        return this.getPriorityValue() - priorityTask.getPriorityValue();
    }
}
