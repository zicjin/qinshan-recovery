/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.mdm.user.service.impl;

import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.mdm.user.dao.CoreUserPermissionTypeDao;
import com.sunwayworld.module.mdm.user.service.CoreUserPermissionTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class CoreUserPermissionTypeServiceImpl
implements CoreUserPermissionTypeService {
    @Autowired
    private CoreUserPermissionTypeDao permissionTypeDao;

    @Override
    public CoreUserPermissionTypeDao getDao() {
        return this.permissionTypeDao;
    }
}
