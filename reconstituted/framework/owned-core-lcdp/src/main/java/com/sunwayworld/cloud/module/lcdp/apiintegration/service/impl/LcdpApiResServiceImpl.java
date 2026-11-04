/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.apiintegration.service.impl;

import com.sunwayworld.cloud.module.lcdp.apiintegration.bean.LcdpApiResBean;
import com.sunwayworld.cloud.module.lcdp.apiintegration.persistent.dao.LcdpApiResDao;
import com.sunwayworld.cloud.module.lcdp.apiintegration.service.LcdpApiResService;
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
public class LcdpApiResServiceImpl
implements LcdpApiResService {
    @Autowired
    private LcdpApiResDao lcdpApiResDao;

    @Override
    public LcdpApiResDao getDao() {
        return this.lcdpApiResDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        LcdpApiResBean lcdpApiRes = jsonWrapper.parseUnique(LcdpApiResBean.class);
        lcdpApiRes.setId(ApplicationContextHelper.getNextIdentity());
        this.getDao().insert(lcdpApiRes);
        return lcdpApiRes.getId();
    }
}
