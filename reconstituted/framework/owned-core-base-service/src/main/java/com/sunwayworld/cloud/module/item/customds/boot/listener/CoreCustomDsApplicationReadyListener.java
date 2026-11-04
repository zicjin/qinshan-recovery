/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.customds.boot.listener;

import com.sunwayworld.cloud.module.item.customds.service.CoreCustomTableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile(value={"custom-ds"})
public class CoreCustomDsApplicationReadyListener
implements ApplicationListener<ApplicationReadyEvent> {
    @Autowired
    private CoreCustomTableService customTableService;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        this.customTableService.initDsTableData();
    }
}
