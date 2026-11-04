/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.admin.config.resource.impl;

import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.admin.config.resource.CoreAdminSelectConfigResource;
import com.sunwayworld.module.admin.config.service.CoreAdminSelectConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@GikamBean
@RestController
public class CoreAdminSelectConfigResourceImpl
implements CoreAdminSelectConfigResource {
    @Autowired
    private CoreAdminSelectConfigService configService;

    @Override
    public CoreAdminSelectConfigService getService() {
        return this.configService;
    }
}
