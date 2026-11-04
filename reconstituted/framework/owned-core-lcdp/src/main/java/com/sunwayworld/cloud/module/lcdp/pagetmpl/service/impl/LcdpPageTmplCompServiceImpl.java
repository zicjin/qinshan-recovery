/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.pagetmpl.service.impl;

import com.sunwayworld.cloud.module.lcdp.pagetmpl.bean.LcdpPageTmplCompBean;
import com.sunwayworld.cloud.module.lcdp.pagetmpl.persistent.dao.LcdpPageTmplCompDao;
import com.sunwayworld.cloud.module.lcdp.pagetmpl.service.LcdpPageTmplCompService;
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
public class LcdpPageTmplCompServiceImpl
implements LcdpPageTmplCompService {
    @Autowired
    private LcdpPageTmplCompDao lcdpPageTmplCompDao;

    @Override
    public LcdpPageTmplCompDao getDao() {
        return this.lcdpPageTmplCompDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.INSERT)
    public String insert(RestJsonWrapperBean jsonWrapper) {
        LcdpPageTmplCompBean lcdpPageTmplComp = jsonWrapper.parseUnique(LcdpPageTmplCompBean.class);
        lcdpPageTmplComp.setId(ApplicationContextHelper.getNextSequence(this.getDao().getTable()));
        this.getDao().insert(lcdpPageTmplComp);
        return lcdpPageTmplComp.getId();
    }
}
