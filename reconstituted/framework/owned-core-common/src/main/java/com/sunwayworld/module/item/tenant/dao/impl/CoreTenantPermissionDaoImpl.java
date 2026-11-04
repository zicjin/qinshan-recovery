/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.item.tenant.dao.impl;

import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.item.tenant.bean.CoreTenantPermissionBean;
import com.sunwayworld.module.item.tenant.dao.CoreTenantPermissionDao;
import com.sunwayworld.module.item.tenant.mapper.CoreTenantPermissionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreTenantPermissionDaoImpl
extends MybatisDaoSupport<CoreTenantPermissionBean, Long>
implements CoreTenantPermissionDao {
    @Autowired
    private CoreTenantPermissionMapper coreTenantPermissionMapper;

    public CoreTenantPermissionMapper getMapper() {
        return this.coreTenantPermissionMapper;
    }
}
