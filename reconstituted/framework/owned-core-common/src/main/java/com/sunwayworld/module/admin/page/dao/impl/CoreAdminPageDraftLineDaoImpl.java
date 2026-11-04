/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.admin.page.dao.impl;

import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.admin.page.bean.CoreAdminPageDraftLineBean;
import com.sunwayworld.module.admin.page.dao.CoreAdminPageDraftLineDao;
import com.sunwayworld.module.admin.page.mapper.CoreAdminPageDraftLineMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreAdminPageDraftLineDaoImpl
extends MybatisDaoSupport<CoreAdminPageDraftLineBean, Long>
implements CoreAdminPageDraftLineDao {
    @Autowired
    private CoreAdminPageDraftLineMapper draftLineMapper;

    public CoreAdminPageDraftLineMapper getMapper() {
        return this.draftLineMapper;
    }
}
