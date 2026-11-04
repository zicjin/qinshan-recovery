/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.role.resource.impl;

import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.sys.role.resource.CoreRoleUserResource;
import com.sunwayworld.module.sys.role.service.CoreRoleUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@LogModule(value="\u89d2\u8272\u7528\u6237")
@RestController
@GikamBean
public class CoreRoleUserResourceImpl
implements CoreRoleUserResource {
    @Autowired
    private CoreRoleUserService roleUserService;

    @Override
    public CoreRoleUserService getService() {
        return this.roleUserService;
    }

    public CoreRoleUserService getMatrixService() {
        return this.roleUserService;
    }

    @Override
    @Log(value="\u8bbe\u7f6e\u9ed8\u8ba4\u89d2\u8272", type=LogType.UPDATE)
    public void setDefault(@PathVariable String userId, RestJsonWrapperBean wrapper) {
        this.getService().setDefault(userId, wrapper);
    }
}
