/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.monitor.boot.listener;

import com.sunwayworld.cloud.module.monitor.monitor.service.CoreMonitorServerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile(value={"monitor"})
public class CoreMonitorApplicationReadyListener
implements ApplicationListener<ApplicationReadyEvent> {
    @Autowired
    private CoreMonitorServerService coreServerService;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        this.coreServerService.insertServerAndApp();
    }
}
