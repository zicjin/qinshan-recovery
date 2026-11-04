/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.schedule.schedule.service;

import java.time.LocalDateTime;

public interface CoreScheduleManager {
    public String schedule(String var1, String var2, String var3, String var4, String var5);

    public void cancel(String var1, String var2);

    public String schedule(String var1, String var2, String var3, String var4, String var5, LocalDateTime var6, LocalDateTime var7);
}
