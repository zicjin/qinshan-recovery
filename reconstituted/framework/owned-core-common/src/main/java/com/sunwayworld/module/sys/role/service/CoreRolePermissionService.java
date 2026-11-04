/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.role.service;

import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.module.sys.role.bean.CoreRolePermissionBean;
import java.util.List;

public interface CoreRolePermissionService
extends GenericService<CoreRolePermissionBean, Long> {
    public List<CoreRolePermissionBean> selectListByRole(Long var1);

    public void insert(Long var1, String var2, List<String> var3);

    public void delete(Long var1, String var2, List<String> var3);

    public void deleteByRoleId(Long var1);

    public List<CoreRolePermissionBean> selectActiveUserPermissionList(String var1, String var2);

    public String selectPermissionTypeAdmin(String var1);
}
