/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.help.resource.impl;

import com.sunwayworld.cloud.module.item.help.bean.CoreHelpBean;
import com.sunwayworld.cloud.module.item.help.resource.CoreHelpResource;
import com.sunwayworld.cloud.module.item.help.service.CoreHelpService;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.base.resource.AbstractGenericResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@LogModule(value="\u5e2e\u52a9")
@RestController
@GikamBean
public class CoreHelpResourceImpl
implements CoreHelpResource,
AbstractGenericResource<CoreHelpService, CoreHelpBean, String> {
    @Autowired
    private CoreHelpService helpService;

    @Override
    public CoreHelpService getService() {
        return this.helpService;
    }
}
