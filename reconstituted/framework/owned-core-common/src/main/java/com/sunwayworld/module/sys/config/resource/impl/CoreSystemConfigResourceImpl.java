/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.config.resource.impl;

import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.sys.config.resource.CoreSystemConfigResource;
import com.sunwayworld.module.sys.config.service.CoreSystemConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@LogModule(value="\u7cfb\u7edf\u914d\u7f6e")
@RestController
@GikamBean
public class CoreSystemConfigResourceImpl
implements CoreSystemConfigResource {
    @Autowired
    private CoreSystemConfigService coreSystemConfigService;

    @Override
    public CoreSystemConfigService getService() {
        return this.coreSystemConfigService;
    }
}
