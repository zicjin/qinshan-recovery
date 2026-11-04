/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.mdm.user.dao.impl;

import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.mdm.user.bean.CoreUserPermissionTypeBean;
import com.sunwayworld.module.mdm.user.dao.CoreUserPermissionTypeDao;
import com.sunwayworld.module.mdm.user.mapper.CoreUserPermissionTypeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreUserPermissionTypeDaoImpl
extends MybatisDaoSupport<CoreUserPermissionTypeBean, String>
implements CoreUserPermissionTypeDao {
    @Autowired
    private CoreUserPermissionTypeMapper userPermissionTypeMapper;

    public CoreUserPermissionTypeMapper getMapper() {
        return this.userPermissionTypeMapper;
    }
}
