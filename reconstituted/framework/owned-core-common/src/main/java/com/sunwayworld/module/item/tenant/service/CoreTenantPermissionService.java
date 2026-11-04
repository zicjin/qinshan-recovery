/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.item.tenant.service;

import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.module.item.tenant.bean.CoreTenantPermissionBean;
import java.util.List;

public interface CoreTenantPermissionService
extends GenericService<CoreTenantPermissionBean, Long> {
    public List<CoreTenantPermissionBean> selectLoginTenantList(RestJsonWrapperBean var1);
}
