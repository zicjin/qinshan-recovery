/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.admin.tenant.dao.impl;

import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.tenant.TenantManager;
import com.sunwayworld.module.admin.tenant.bean.CoreAdminTenantBean;
import com.sunwayworld.module.admin.tenant.dao.CoreAdminTenantDao;
import com.sunwayworld.module.admin.tenant.mapper.CoreAdminTenantMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreAdminTenantDaoImpl
extends MybatisDaoSupport<CoreAdminTenantBean, String>
implements CoreAdminTenantDao {
    @Autowired
    private CoreAdminTenantMapper tenantMapper;
    @Lazy
    @Autowired
    private TenantManager tenantManager;

    public CoreAdminTenantMapper getMapper() {
        return this.tenantMapper;
    }

    @Override
    @CacheEvict(value={"T_CORE_ADMIN_TENANT"}, key="'ALL'")
    public void cacheEvict(CoreAdminTenantBean oldItem, CoreAdminTenantBean newItem) {
        this.tenantManager.reload();
    }
}
