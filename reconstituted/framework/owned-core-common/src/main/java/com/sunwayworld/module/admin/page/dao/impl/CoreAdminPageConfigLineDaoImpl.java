/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.admin.page.dao.impl;

import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.admin.page.bean.CoreAdminPageConfigLineBean;
import com.sunwayworld.module.admin.page.dao.CoreAdminPageConfigLineDao;
import com.sunwayworld.module.admin.page.mapper.CoreAdminPageConfigLineMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreAdminPageConfigLineDaoImpl
extends MybatisDaoSupport<CoreAdminPageConfigLineBean, Long>
implements CoreAdminPageConfigLineDao {
    @Autowired
    private CoreAdminPageConfigLineMapper configLineMapper;

    public CoreAdminPageConfigLineMapper getMapper() {
        return this.configLineMapper;
    }
}
