/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.admin.config.resource.impl;

import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.admin.config.resource.CoreAdminCheckConfigResource;
import com.sunwayworld.module.admin.config.service.CoreAdminCheckConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@GikamBean
@RestController
public class CoreAdminCheckConfigResourceImpl
implements CoreAdminCheckConfigResource {
    @Autowired
    private CoreAdminCheckConfigService configService;

    @Override
    public CoreAdminCheckConfigService getService() {
        return this.configService;
    }
}
