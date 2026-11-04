/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.mdm.user.service;

import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.activatable.service.GenericActivatableService;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.module.mdm.user.bean.CoreModifiedUserBean;
import com.sunwayworld.module.mdm.user.bean.CoreModifiedUserPermissionBean;
import com.sunwayworld.module.mdm.user.bean.CoreModifiedUserRoleBean;

public interface CoreModifiedUserService
extends GenericService<CoreModifiedUserBean, Long>,
GenericActivatableService<CoreModifiedUserBean, Long> {
    public Long insertByUserId(String var1);

    public void insertUserPermission(Long var1, String var2, RestJsonWrapperBean var3);

    public void deleteUserPermission(Long var1, RestJsonWrapperBean var2);

    public Page<CoreModifiedUserPermissionBean> selectUserPermissionPagination(Long var1, String var2, RestJsonWrapperBean var3);

    public Page<CoreModifiedUserRoleBean> selectUserRolePagination(Long var1, RestJsonWrapperBean var2);

    public Page<CoreModifiedUserBean> selectActivatedPagination(RestJsonWrapperBean var1);

    public Page<CoreModifiedUserBean> selectExecutedPagination(RestJsonWrapperBean var1);

    public void insertUserRoles(Long var1, RestJsonWrapperBean var2);

    public void deleteUserRoles(Long var1, RestJsonWrapperBean var2);

    public void setUserRoleDefault(Long var1, Long var2);

    public void setUserPermissionDefault(Long var1, Long var2);

    public void executeScheduled(Long var1);
}
