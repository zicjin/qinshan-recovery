/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.role.resource;

import com.sunwayworld.framework.support.base.resource.GenericResource;
import com.sunwayworld.framework.support.selectable.resource.GenericSelectableResource;
import com.sunwayworld.module.sys.role.bean.CoreRolePermissionTypeBean;
import com.sunwayworld.module.sys.role.service.CoreRolePermissionTypeService;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(value={"/core/module/sys/role-permission-types"})
public interface CoreRolePermissionTypeResource
extends GenericResource<CoreRolePermissionTypeService, CoreRolePermissionTypeBean, String>,
GenericSelectableResource<CoreRolePermissionTypeService, CoreRolePermissionTypeBean, String> {
}
