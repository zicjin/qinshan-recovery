/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.sys.monitor.pm.resource.impl;

import com.sunwayworld.cloud.module.sys.monitor.pm.bean.CoreSystemMonitorSqlBean;
import com.sunwayworld.cloud.module.sys.monitor.pm.resource.CoreSystemMonitorSqlResource;
import com.sunwayworld.cloud.module.sys.monitor.pm.service.CoreSystemMonitorSqlService;
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

@LogModule(value="\u6162sql\u8bb0\u5f55\u8868")
@RestController
@GikamBean
public class CoreSystemMonitorSqlResourceImpl
implements CoreSystemMonitorSqlResource,
AbstractGenericResource<CoreSystemMonitorSqlService, CoreSystemMonitorSqlBean, Long> {
    @Autowired
    private CoreSystemMonitorSqlService coreSystemMonitorSqlService;

    @Override
    public CoreSystemMonitorSqlService getService() {
        return this.coreSystemMonitorSqlService;
    }

    @Override
    @Log(value="\u4fee\u8ba2", type=LogType.UPDATE)
    @RequestMapping(value={"/action/revise"}, method={RequestMethod.PUT})
    public void revise(RestJsonWrapperBean wrapper) {
        this.getService().revise(wrapper);
    }

    @Override
    @Log(value="\u53d6\u6d88\u4fee\u8ba2", type=LogType.UPDATE)
    @RequestMapping(value={"/action/cancel-revise"}, method={RequestMethod.PUT})
    public void cancelRevise(RestJsonWrapperBean wrapper) {
        this.getService().cancelRevise(wrapper);
    }

    @Override
    @Log(value="\u6e05\u7a7a", type=LogType.DELETE)
    @RequestMapping(value={"/action/empty"}, method={RequestMethod.DELETE})
    public void cleanAll() {
        this.getService().cleanAll();
    }
}
