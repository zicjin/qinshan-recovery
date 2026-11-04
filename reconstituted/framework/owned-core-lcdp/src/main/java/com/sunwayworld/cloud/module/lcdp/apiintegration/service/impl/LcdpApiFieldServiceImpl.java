/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.apiintegration.service.impl;

import com.sunwayworld.cloud.module.lcdp.apiintegration.bean.LcdpApiFieldBean;
import com.sunwayworld.cloud.module.lcdp.apiintegration.persistent.dao.LcdpApiFieldDao;
import com.sunwayworld.cloud.module.lcdp.apiintegration.service.LcdpApiFieldService;
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
public class LcdpApiFieldServiceImpl
implements LcdpApiFieldService {
    @Autowired
    private LcdpApiFieldDao lcdpApiFieldDao;

    @Override
    public LcdpApiFieldDao getDao() {
        return this.lcdpApiFieldDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        LcdpApiFieldBean lcdpApiField = jsonWrapper.parseUnique(LcdpApiFieldBean.class);
        lcdpApiField.setId(ApplicationContextHelper.getNextIdentity());
        this.getDao().insert(lcdpApiField);
        return lcdpApiField.getId();
    }
}
