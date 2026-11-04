/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.mdm.user.resource;

import com.sunwayworld.framework.support.base.resource.GenericResource;
import com.sunwayworld.framework.support.selectable.resource.GenericSelectableResource;
import com.sunwayworld.module.mdm.user.bean.CoreUserPermissionTypeBean;
import com.sunwayworld.module.mdm.user.service.CoreUserPermissionTypeService;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(value={"/secure/core/module/mdm/user-permission-types"})
public interface CoreUserPermissionTypeResource
extends GenericResource<CoreUserPermissionTypeService, CoreUserPermissionTypeBean, String>,
GenericSelectableResource<CoreUserPermissionTypeService, CoreUserPermissionTypeBean, String> {
}
