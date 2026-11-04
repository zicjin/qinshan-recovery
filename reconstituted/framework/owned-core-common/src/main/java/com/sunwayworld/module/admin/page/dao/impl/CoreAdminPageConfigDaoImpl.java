/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.admin.page.dao.impl;

import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.admin.page.bean.CoreAdminPageConfigBean;
import com.sunwayworld.module.admin.page.dao.CoreAdminPageConfigDao;
import com.sunwayworld.module.admin.page.mapper.CoreAdminPageConfigMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreAdminPageConfigDaoImpl
extends MybatisDaoSupport<CoreAdminPageConfigBean, Long>
implements CoreAdminPageConfigDao {
    @Autowired
    private CoreAdminPageConfigMapper configMapper;

    public CoreAdminPageConfigMapper getMapper() {
        return this.configMapper;
    }
}
