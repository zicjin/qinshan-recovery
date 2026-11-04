/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.config.resource.impl;

import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.sys.config.resource.CoreLoginConfigResource;
import com.sunwayworld.module.sys.config.service.CoreLoginConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@LogModule(value="\u767b\u5f55\u914d\u7f6e")
@RestController
@GikamBean
public class CoreLoginConfigResourceImpl
implements CoreLoginConfigResource {
    @Autowired
    private CoreLoginConfigService coreLoginConfigService;

    @Override
    public CoreLoginConfigService getService() {
        return this.coreLoginConfigService;
    }
}
