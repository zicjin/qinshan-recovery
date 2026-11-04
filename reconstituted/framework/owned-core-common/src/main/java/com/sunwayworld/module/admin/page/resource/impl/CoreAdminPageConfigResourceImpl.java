/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.admin.page.resource.impl;

import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.admin.page.resource.CoreAdminPageConfigResource;
import com.sunwayworld.module.admin.page.service.CoreAdminPageConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@GikamBean
@RestController
public class CoreAdminPageConfigResourceImpl
implements CoreAdminPageConfigResource {
    @Autowired
    private CoreAdminPageConfigService configService;

    @Override
    public CoreAdminPageConfigService getService() {
        return this.configService;
    }
}
