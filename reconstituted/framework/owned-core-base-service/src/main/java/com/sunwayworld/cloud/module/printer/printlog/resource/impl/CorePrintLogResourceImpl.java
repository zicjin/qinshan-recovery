/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.printer.printlog.resource.impl;

import com.sunwayworld.cloud.module.printer.printlog.bean.CorePrintLogBean;
import com.sunwayworld.cloud.module.printer.printlog.resource.CorePrintLogResource;
import com.sunwayworld.cloud.module.printer.printlog.service.CorePrintLogService;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.base.resource.AbstractGenericResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@LogModule(value="\u6253\u5370\u65e5\u5fd7")
@RestController
@GikamBean
public class CorePrintLogResourceImpl
implements CorePrintLogResource,
AbstractGenericResource<CorePrintLogService, CorePrintLogBean, Long> {
    @Autowired
    private CorePrintLogService printLogService;

    @Override
    public CorePrintLogService getService() {
        return this.printLogService;
    }

    @Override
    @Log(value="\u65b0\u589e\u6253\u5370\u65e5\u5fd7", type=LogType.INSERT)
    public Long insert(RestJsonWrapperBean wrapper) {
        return (Long)this.printLogService.insert(wrapper);
    }
}
