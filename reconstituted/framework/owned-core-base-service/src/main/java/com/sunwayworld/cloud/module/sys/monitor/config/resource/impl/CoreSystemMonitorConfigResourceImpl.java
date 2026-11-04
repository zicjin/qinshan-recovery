/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.sys.monitor.config.resource.impl;

import com.sunwayworld.cloud.module.sys.monitor.config.bean.CoreSystemMonitorConfigBean;
import com.sunwayworld.cloud.module.sys.monitor.config.resource.CoreSystemMonitorConfigResource;
import com.sunwayworld.cloud.module.sys.monitor.config.service.CoreSystemMonitorConfigService;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.base.resource.AbstractGenericResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@LogModule(value="\u7cfb\u7edf\u76d1\u63a7\u914d\u7f6e")
@RestController
@GikamBean
@RequestMapping(value={"/secure/module/sys/monitor/configs"})
public class CoreSystemMonitorConfigResourceImpl
implements CoreSystemMonitorConfigResource,
AbstractGenericResource<CoreSystemMonitorConfigService, CoreSystemMonitorConfigBean, String> {
    @Autowired
    private CoreSystemMonitorConfigService coreSystemMonitorConfigService;

    @Override
    public CoreSystemMonitorConfigService getService() {
        return this.coreSystemMonitorConfigService;
    }
}
