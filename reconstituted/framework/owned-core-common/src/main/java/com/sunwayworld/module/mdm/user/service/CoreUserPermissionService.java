/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.mdm.user.service;

import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.module.mdm.user.bean.CoreUserPermissionBean;
import java.util.List;

public interface CoreUserPermissionService
extends GenericService<CoreUserPermissionBean, Long> {
    public void insert(String var1, String var2, RestJsonWrapperBean var3);

    public Page<CoreUserPermissionBean> selectPaginationByPermissionType(String var1, RestJsonWrapperBean var2);

    public List<CoreUserPermissionBean> selectLoginPermissionList(RestJsonWrapperBean var1, String var2);
}
