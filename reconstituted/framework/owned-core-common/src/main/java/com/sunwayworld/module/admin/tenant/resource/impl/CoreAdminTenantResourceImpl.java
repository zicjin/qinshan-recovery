/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.admin.tenant.resource.impl;

import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.admin.tenant.bean.CoreAdminTenantUserBean;
import com.sunwayworld.module.admin.tenant.resource.CoreAdminTenantResource;
import com.sunwayworld.module.admin.tenant.service.CoreAdminTenantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.RestController;

@GikamBean
@Profile(value={"tenant"})
@RestController
public class CoreAdminTenantResourceImpl
implements CoreAdminTenantResource {
    @Autowired
    private CoreAdminTenantService tenantService;

    @Override
    public CoreAdminTenantService getService() {
        return this.tenantService;
    }

    @Override
    public void insertUsers(String id, RestJsonWrapperBean jsonWrapper) {
        this.getService().insertUsers(id, jsonWrapper);
    }

    @Override
    public void deleteUsers(String id, RestJsonWrapperBean jsonWrapper) {
        this.getService().deleteUsers(id, jsonWrapper);
    }

    @Override
    public Page<CoreAdminTenantUserBean> selectUserPagination(String id, RestJsonWrapperBean wrapper) {
        return this.getService().selectUserPagination(id, wrapper);
    }
}
