/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.role.service;

import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.framework.support.selectable.service.GenericSelectableService;
import com.sunwayworld.module.sys.role.bean.CoreRolePermissionTypeBean;
import java.util.List;

public interface CoreRolePermissionTypeService
extends GenericService<CoreRolePermissionTypeBean, String>,
GenericSelectableService<CoreRolePermissionTypeBean, String> {
    public List<CoreRolePermissionTypeBean> selectListByPermissionType(String var1);
}
