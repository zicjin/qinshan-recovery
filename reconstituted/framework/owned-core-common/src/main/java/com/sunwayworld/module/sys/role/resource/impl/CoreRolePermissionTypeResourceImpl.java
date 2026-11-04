/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.role.resource.impl;

import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.sys.role.resource.CoreRolePermissionTypeResource;
import com.sunwayworld.module.sys.role.service.CoreRolePermissionTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@LogModule(value="\u89d2\u8272\u6743\u9650\u7c7b\u578b")
@RestController
@GikamBean
public class CoreRolePermissionTypeResourceImpl
implements CoreRolePermissionTypeResource {
    @Autowired
    private CoreRolePermissionTypeService permissionTypeService;

    @Override
    public CoreRolePermissionTypeService getService() {
        return this.permissionTypeService;
    }
}
