/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.pagetmpl.service.impl;

import com.sunwayworld.cloud.module.lcdp.pagetmpl.bean.LcdpPageTmplConfigBean;
import com.sunwayworld.cloud.module.lcdp.pagetmpl.persistent.dao.LcdpPageTmplConfigDao;
import com.sunwayworld.cloud.module.lcdp.pagetmpl.service.LcdpPageTmplConfigService;
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
public class LcdpPageTmplConfigServiceImpl
implements LcdpPageTmplConfigService {
    @Autowired
    private LcdpPageTmplConfigDao lcdpPageTmplConfigDao;

    @Override
    public LcdpPageTmplConfigDao getDao() {
        return this.lcdpPageTmplConfigDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        LcdpPageTmplConfigBean lcdpPageTmplConfig = jsonWrapper.parseUnique(LcdpPageTmplConfigBean.class);
        lcdpPageTmplConfig.setId(ApplicationContextHelper.getNextIdentity());
        this.getDao().insert(lcdpPageTmplConfig);
        return lcdpPageTmplConfig.getId();
    }
}
