/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.log.resource.impl;

import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.sys.log.resource.CoreLogResource;
import com.sunwayworld.module.sys.log.service.CoreLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@LogModule(value="\u7cfb\u7edf\u65e5\u5fd7")
@RestController
@GikamBean
public class CoreLogResourceImpl
implements CoreLogResource {
    @Autowired
    private CoreLogService logService;

    @Override
    public CoreLogService getService() {
        return this.logService;
    }
}
