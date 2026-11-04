/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.monitor.monitor.resource.impl;

import com.sunwayworld.cloud.module.monitor.monitor.bean.CoreMonitorServerBean;
import com.sunwayworld.cloud.module.monitor.monitor.resource.CoreMonitorResource;
import com.sunwayworld.cloud.module.monitor.monitor.service.CoreMonitorServerService;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.base.resource.AbstractGenericResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@LogModule(value="\u670d\u52a1\u5668\u4fe1\u606f")
@RestController
@GikamBean
public class CoreMonitorResourceImpl
implements CoreMonitorResource,
AbstractGenericResource<CoreMonitorServerService, CoreMonitorServerBean, String> {
    @Autowired
    private CoreMonitorServerService coreServerService;

    @Override
    public CoreMonitorServerService getService() {
        return this.coreServerService;
    }
}
