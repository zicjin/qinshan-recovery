/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.constant.resource.impl;

import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.sys.constant.resource.CoreConstantResource;
import com.sunwayworld.module.sys.constant.service.CoreConstantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@LogModule(value="\u7cfb\u7edf\u53c2\u6570")
@RestController
@GikamBean
public class CoreConstantResourceImpl
implements CoreConstantResource {
    @Autowired
    private CoreConstantService service;

    @Override
    public CoreConstantService getService() {
        return this.service;
    }
}
