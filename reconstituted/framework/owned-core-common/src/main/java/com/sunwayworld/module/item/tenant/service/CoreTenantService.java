/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.item.tenant.service;

import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.module.item.tenant.bean.CoreTenantBean;
import com.sunwayworld.module.item.tenant.bean.CoreTenantPermissionBean;
import java.util.List;

public interface CoreTenantService
extends GenericService<CoreTenantBean, Long> {
    public Page<CoreTenantPermissionBean> selectPermissionPaginationByTenantId(Long var1, RestJsonWrapperBean var2);

    public Long insertPermission(Long var1, RestJsonWrapperBean var2);

    public void deletePermission(Long var1, RestJsonWrapperBean var2);

    public List<CoreTenantPermissionBean> selectLoginTenantList(RestJsonWrapperBean var1);
}
