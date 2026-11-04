/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.status.resource.impl;

import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.sys.status.resource.CoreStatusResource;
import com.sunwayworld.module.sys.status.service.CoreStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
@GikamBean
public class CoreStatusResourceImpl
implements CoreStatusResource {
    @Autowired
    private CoreStatusService statusService;

    @Override
    public String selectSpeed() {
        return this.statusService.selectSpeed();
    }

    @Override
    public void keepOnline() {
        this.statusService.keepOnline();
    }
}
