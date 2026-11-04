/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.admin.menu.resource.impl;

import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.admin.menu.resource.CoreAdminMenuResource;
import com.sunwayworld.module.admin.menu.service.CoreAdminMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@GikamBean
@RestController
public class CoreAdminMenuResourceImpl
implements CoreAdminMenuResource {
    @Autowired
    private CoreAdminMenuService menuService;

    @Override
    public CoreAdminMenuService getService() {
        return this.menuService;
    }
}
