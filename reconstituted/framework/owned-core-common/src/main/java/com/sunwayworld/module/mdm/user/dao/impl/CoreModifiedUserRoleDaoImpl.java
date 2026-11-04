/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.mdm.user.dao.impl;

import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.mdm.user.bean.CoreModifiedUserRoleBean;
import com.sunwayworld.module.mdm.user.dao.CoreModifiedUserRoleDao;
import com.sunwayworld.module.mdm.user.mapper.CoreModifiedUserRoleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreModifiedUserRoleDaoImpl
extends MybatisDaoSupport<CoreModifiedUserRoleBean, Long>
implements CoreModifiedUserRoleDao {
    @Autowired
    private CoreModifiedUserRoleMapper modifiedUserRoleMapper;

    public CoreModifiedUserRoleMapper getMapper() {
        return this.modifiedUserRoleMapper;
    }
}
