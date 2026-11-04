/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.moduletmpl.service.impl;

import com.sunwayworld.cloud.module.lcdp.moduletmpl.bean.LcdpModuleTmplResourceBean;
import com.sunwayworld.cloud.module.lcdp.moduletmpl.persistent.dao.LcdpModuleTmplResourceDao;
import com.sunwayworld.cloud.module.lcdp.moduletmpl.service.LcdpModuleTmplResourceService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@GikamBean
public class LcdpModuleTmplResourceServiceImpl
implements LcdpModuleTmplResourceService {
    @Autowired
    private LcdpModuleTmplResourceDao lcdpModuleTmplResourceDao;

    @Override
    public LcdpModuleTmplResourceDao getDao() {
        return this.lcdpModuleTmplResourceDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        LcdpModuleTmplResourceBean lcdpModuleTmplResource = jsonWrapper.parseUnique(LcdpModuleTmplResourceBean.class);
        lcdpModuleTmplResource.setId(ApplicationContextHelper.getNextIdentity());
        this.getDao().insert(lcdpModuleTmplResource);
        return lcdpModuleTmplResource.getId();
    }
}
