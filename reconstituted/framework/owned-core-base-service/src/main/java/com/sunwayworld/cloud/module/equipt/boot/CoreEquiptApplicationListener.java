/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.equipt.boot;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;

@Service
public class CoreEquiptApplicationListener
implements ApplicationListener<ApplicationReadyEvent> {
    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
    }
}
