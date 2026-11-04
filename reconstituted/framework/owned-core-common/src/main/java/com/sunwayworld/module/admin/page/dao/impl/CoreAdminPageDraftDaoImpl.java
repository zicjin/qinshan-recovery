/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.admin.page.dao.impl;

import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.admin.page.bean.CoreAdminPageDraftBean;
import com.sunwayworld.module.admin.page.dao.CoreAdminPageDraftDao;
import com.sunwayworld.module.admin.page.mapper.CoreAdminPageDraftMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreAdminPageDraftDaoImpl
extends MybatisDaoSupport<CoreAdminPageDraftBean, Long>
implements CoreAdminPageDraftDao {
    @Autowired
    private CoreAdminPageDraftMapper draftMapper;

    public CoreAdminPageDraftMapper getMapper() {
        return this.draftMapper;
    }
}
