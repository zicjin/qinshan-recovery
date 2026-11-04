/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.checkoutrecord.service.impl;

import com.sunwayworld.cloud.module.lcdp.checkoutrecord.bean.LcdpCrdLogBean;
import com.sunwayworld.cloud.module.lcdp.checkoutrecord.persistent.dao.LcdpCrdLogDao;
import com.sunwayworld.cloud.module.lcdp.checkoutrecord.service.LcdpCrdLogService;
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
public class LcdpCrdLogServiceImpl
implements LcdpCrdLogService {
    @Autowired
    private LcdpCrdLogDao lcdpCrdLogDao;

    @Override
    public LcdpCrdLogDao getDao() {
        return this.lcdpCrdLogDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        LcdpCrdLogBean lcdpCrdLog = jsonWrapper.parseUnique(LcdpCrdLogBean.class);
        lcdpCrdLog.setId(ApplicationContextHelper.getNextIdentity());
        this.getDao().insert(lcdpCrdLog);
        return lcdpCrdLog.getId();
    }
}
