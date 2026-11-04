/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.item.tenant.resource;

import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.resource.GenericResource;
import com.sunwayworld.module.item.tenant.bean.CoreTenantBean;
import com.sunwayworld.module.item.tenant.bean.CoreTenantPermissionBean;
import com.sunwayworld.module.item.tenant.service.CoreTenantService;
import java.util.List;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(value={"/secure/core/module/item/tenants"})
public interface CoreTenantResource
extends GenericResource<CoreTenantService, CoreTenantBean, Long> {
    public Long insertPermission(Long var1, RestJsonWrapperBean var2);

    public void deletePermission(Long var1, RestJsonWrapperBean var2);

    public Page<CoreTenantPermissionBean> selectPermissionPaginationByTenantId(Long var1, RestJsonWrapperBean var2);

    public List<CoreTenantPermissionBean> selectLoginTenantList(RestJsonWrapperBean var1);
}
