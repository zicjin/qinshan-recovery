/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.mdm.user.resource.impl;

import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.mdm.user.resource.CoreUserPermissionTypeResource;
import com.sunwayworld.module.mdm.user.service.CoreUserPermissionTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@LogModule(value="\u7528\u6237\u6743\u9650\u7c7b\u578b")
@RestController
@GikamBean
public class CoreUserPermissionTypeResourceImpl
implements CoreUserPermissionTypeResource {
    @Autowired
    private CoreUserPermissionTypeService userPermissionTypeService;

    @Override
    public CoreUserPermissionTypeService getService() {
        return this.userPermissionTypeService;
    }
}
