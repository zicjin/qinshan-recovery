/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.mdm.user.service;

import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.module.mdm.user.bean.CoreModifiedUserPermissionBean;
import java.util.List;

public interface CoreModifiedUserPermissionService
extends GenericService<CoreModifiedUserPermissionBean, Long> {
    public void insert(Long var1, String var2, RestJsonWrapperBean var3);

    public void delete(Long var1, RestJsonWrapperBean var2);

    public Page<CoreModifiedUserPermissionBean> selectPaginationByPermissionType(Long var1, String var2, RestJsonWrapperBean var3);

    public void setUserPermissionDefault(Long var1, Long var2);

    public List<CoreModifiedUserPermissionBean> selectPermissionList(String var1, MapperParameter var2);
}
