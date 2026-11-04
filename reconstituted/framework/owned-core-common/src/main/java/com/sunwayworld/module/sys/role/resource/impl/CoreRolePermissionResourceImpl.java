/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.role.resource.impl;

import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.sys.role.resource.CoreRolePermissionResource;
import com.sunwayworld.module.sys.role.service.CoreRolePermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@LogModule(value="\u89d2\u8272\u6743\u9650")
@RestController
@GikamBean
public class CoreRolePermissionResourceImpl
implements CoreRolePermissionResource {
    @Autowired
    private CoreRolePermissionService permissionService;

    @Override
    public CoreRolePermissionService getService() {
        return this.permissionService;
    }

    @Override
    @Log(value="\u67e5\u8be2\u5f53\u524d\u767b\u5f55\u7528\u6237\u662f\u5426\u4e3a\u6743\u9650\u7c7b\u578b\u7ba1\u7406\u5458", type=LogType.SELECT)
    @RequestMapping(value={"/permision-type-admin/{permisionTypeId}"}, method={RequestMethod.GET})
    public String selectPermissionTypeAdmin(@PathVariable String permisionTypeId) {
        return this.getService().selectPermissionTypeAdmin(permisionTypeId);
    }
}
