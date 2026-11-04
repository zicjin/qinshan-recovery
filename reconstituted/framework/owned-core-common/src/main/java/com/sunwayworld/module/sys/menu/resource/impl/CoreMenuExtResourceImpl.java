/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.menu.resource.impl;

import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.sys.menu.resource.CoreMenuExtResource;
import com.sunwayworld.module.sys.menu.service.CoreMenuExtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@LogModule(value="\u7cfb\u7edf\u83dc\u5355\u6269\u5c55")
@RestController
@GikamBean
public class CoreMenuExtResourceImpl
implements CoreMenuExtResource {
    @Autowired
    private CoreMenuExtService menuExtService;

    @Override
    public CoreMenuExtService getService() {
        return this.menuExtService;
    }
}
