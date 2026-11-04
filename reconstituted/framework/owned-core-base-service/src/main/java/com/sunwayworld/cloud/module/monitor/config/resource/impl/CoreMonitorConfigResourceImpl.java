/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.monitor.config.resource.impl;

import com.sunwayworld.cloud.module.monitor.config.bean.CoreMonitorConfigBean;
import com.sunwayworld.cloud.module.monitor.config.resource.CoreMonitorConfigResource;
import com.sunwayworld.cloud.module.monitor.config.service.CoreMonitorConfigService;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.base.resource.AbstractGenericResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@LogModule(value="\u7cfb\u7edf\u76d1\u63a7\u914d\u7f6e")
@RestController
@GikamBean
public class CoreMonitorConfigResourceImpl
implements CoreMonitorConfigResource,
AbstractGenericResource<CoreMonitorConfigService, CoreMonitorConfigBean, String> {
    @Autowired
    private CoreMonitorConfigService coreMonitorConfigService;

    @Override
    public CoreMonitorConfigService getService() {
        return this.coreMonitorConfigService;
    }

    @Override
    @Log(value="\u65b0\u589e\u7cfb\u7edf\u76d1\u63a7\u914d\u7f6e", type=LogType.INSERT)
    @RequestMapping(method={RequestMethod.POST})
    public String insert(RestJsonWrapperBean wrapper) {
        return (String)this.getService().insert(wrapper);
    }
}
