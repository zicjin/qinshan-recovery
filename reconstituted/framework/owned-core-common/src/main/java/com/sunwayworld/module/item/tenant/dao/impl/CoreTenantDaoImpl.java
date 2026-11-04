/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.item.tenant.dao.impl;

import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.item.tenant.bean.CoreTenantBean;
import com.sunwayworld.module.item.tenant.dao.CoreTenantDao;
import com.sunwayworld.module.item.tenant.mapper.CoreTenantMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreTenantDaoImpl
extends MybatisDaoSupport<CoreTenantBean, Long>
implements CoreTenantDao {
    @Autowired
    private CoreTenantMapper coreTenantMapper;

    public CoreTenantMapper getMapper() {
        return this.coreTenantMapper;
    }
}
