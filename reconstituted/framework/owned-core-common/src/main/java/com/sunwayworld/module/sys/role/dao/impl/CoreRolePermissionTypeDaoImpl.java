/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.role.dao.impl;

import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.sys.role.bean.CoreRolePermissionTypeBean;
import com.sunwayworld.module.sys.role.dao.CoreRolePermissionTypeDao;
import com.sunwayworld.module.sys.role.mapper.CoreRolePermissionTypeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class CoreRolePermissionTypeDaoImpl
extends MybatisDaoSupport<CoreRolePermissionTypeBean, String>
implements CoreRolePermissionTypeDao {
    @Autowired
    private CoreRolePermissionTypeMapper permissionTypeMapper;

    public CoreRolePermissionTypeMapper getMapper() {
        return this.permissionTypeMapper;
    }
}
