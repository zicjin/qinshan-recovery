/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.admin.tenant.dao.impl;

import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.admin.tenant.bean.CoreAdminTenantUserBean;
import com.sunwayworld.module.admin.tenant.dao.CoreAdminTenantUserDao;
import com.sunwayworld.module.admin.tenant.mapper.CoreAdminTenantUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreAdminTenantUserDaoImpl
extends MybatisDaoSupport<CoreAdminTenantUserBean, Long>
implements CoreAdminTenantUserDao {
    @Autowired
    private CoreAdminTenantUserMapper tenantUserMapper;

    public CoreAdminTenantUserMapper getMapper() {
        return this.tenantUserMapper;
    }
}
