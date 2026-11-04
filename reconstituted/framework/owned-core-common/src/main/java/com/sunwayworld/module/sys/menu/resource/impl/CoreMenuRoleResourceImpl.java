/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.menu.resource.impl;

import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.sys.menu.resource.CoreMenuRoleResource;
import com.sunwayworld.module.sys.menu.service.CoreMenuRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@LogModule(value="\u83dc\u5355\u89d2\u8272")
@RestController
@GikamBean
public class CoreMenuRoleResourceImpl
implements CoreMenuRoleResource {
    @Autowired
    private CoreMenuRoleService menuRoleService;

    public CoreMenuRoleService getMatrixService() {
        return this.menuRoleService;
    }

    @Override
    @Log(value="\u5b9e\u65f6\u4fdd\u5b58", type=LogType.UPDATE)
    public void instantSave(RestJsonWrapperBean wrapper) {
        this.menuRoleService.instantSave(wrapper);
    }
}
