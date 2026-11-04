/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.admin.tenant.service.impl;

import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.admin.tenant.dao.CoreAdminTenantUserDao;
import com.sunwayworld.module.admin.tenant.service.CoreAdminTenantUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreAdminTenantUserServiceImpl
implements CoreAdminTenantUserService {
    @Autowired
    private CoreAdminTenantUserDao tenantUserDao;

    @Override
    public CoreAdminTenantUserDao getDao() {
        return this.tenantUserDao;
    }
}
