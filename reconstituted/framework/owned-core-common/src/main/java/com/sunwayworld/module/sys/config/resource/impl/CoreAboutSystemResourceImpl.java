/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.config.resource.impl;

import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.sys.config.resource.CoreAboutSystemResource;
import com.sunwayworld.module.sys.config.service.CoreAboutSystemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@LogModule(value="\u7cfb\u7edf\u5173\u4e8e")
@RestController
@GikamBean
public class CoreAboutSystemResourceImpl
implements CoreAboutSystemResource {
    @Autowired
    private CoreAboutSystemService coreAboutSystemService;

    @Override
    public CoreAboutSystemService getService() {
        return this.coreAboutSystemService;
    }
}
