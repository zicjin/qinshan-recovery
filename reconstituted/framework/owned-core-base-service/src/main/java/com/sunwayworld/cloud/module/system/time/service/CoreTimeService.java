/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.system.time.service;

import java.time.LocalDateTime;

public interface CoreTimeService {
    public LocalDateTime getServerTime();

    public void checkTimeSimultaneous();
}
