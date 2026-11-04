/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.design.studio.dao.impl;

import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.design.studio.bean.CoreStudioPageDraftBean;
import com.sunwayworld.module.design.studio.dao.CoreStudioPageDraftDao;
import com.sunwayworld.module.design.studio.mapper.CoreStudioPageDraftMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class CoreStudioDraftDaoImpl
extends MybatisDaoSupport<CoreStudioPageDraftBean, Long>
implements CoreStudioPageDraftDao {
    @Autowired
    private CoreStudioPageDraftMapper pageDraftMapper;

    public CoreStudioPageDraftMapper getMapper() {
        return this.pageDraftMapper;
    }
}
