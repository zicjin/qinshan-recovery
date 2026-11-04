/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.role.resource;

import com.sunwayworld.framework.support.base.resource.GenericResource;
import com.sunwayworld.module.sys.role.bean.CoreRolePermissionBean;
import com.sunwayworld.module.sys.role.service.CoreRolePermissionService;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(value={"/core/module/sys/role-permissions"})
public interface CoreRolePermissionResource
extends GenericResource<CoreRolePermissionService, CoreRolePermissionBean, Long> {
    public String selectPermissionTypeAdmin(String var1);
}
