/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.mdm.user.service;

import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.module.mdm.user.bean.CoreModifiedUserRoleBean;

public interface CoreModifiedUserRoleService
extends GenericService<CoreModifiedUserRoleBean, Long> {
    public void insert(Long var1, RestJsonWrapperBean var2);

    public void delete(Long var1, RestJsonWrapperBean var2);

    public Page<CoreModifiedUserRoleBean> selectPaginationByModifiedUserId(Long var1, RestJsonWrapperBean var2);

    public void setUserRoleDefault(Long var1, Long var2);
}
