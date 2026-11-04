/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.admin.mybatis.dao.impl;

import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.admin.mybatis.bean.CoreAdminMybatisDraftBean;
import com.sunwayworld.module.admin.mybatis.dao.CoreAdminMybatisDraftDao;
import com.sunwayworld.module.admin.mybatis.mapper.CoreAdminMybatisDraftMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreAdminMybatisDraftDaoImpl
extends MybatisDaoSupport<CoreAdminMybatisDraftBean, Long>
implements CoreAdminMybatisDraftDao {
    @Autowired
    private CoreAdminMybatisDraftMapper draftMapper;

    public CoreAdminMybatisDraftMapper getMapper() {
        return this.draftMapper;
    }
}
