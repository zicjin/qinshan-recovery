/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.id.schedule;

import com.sunwayworld.framework.id.GikamIdHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class SunwayIdSchedule {
    @Scheduled(fixedDelay=300000L)
    public void schedule() {
        GikamIdHelper.resetTimeout();
    }
}
