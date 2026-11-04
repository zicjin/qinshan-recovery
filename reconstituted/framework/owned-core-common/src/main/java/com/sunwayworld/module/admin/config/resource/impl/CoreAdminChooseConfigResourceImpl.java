/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.admin.config.resource.impl;

import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.admin.config.resource.CoreAdminChooseConfigResource;
import com.sunwayworld.module.admin.config.service.CoreAdminChooseConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@GikamBean
@RestController
public class CoreAdminChooseConfigResourceImpl
implements CoreAdminChooseConfigResource {
    @Autowired
    private CoreAdminChooseConfigService configService;

    @Override
    public CoreAdminChooseConfigService getService() {
        return this.configService;
    }
}
