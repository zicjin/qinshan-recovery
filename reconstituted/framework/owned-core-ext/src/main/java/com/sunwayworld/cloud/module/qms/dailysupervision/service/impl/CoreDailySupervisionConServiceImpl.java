/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.dailysupervision.service.impl;

import com.sunwayworld.cloud.module.qms.dailysupervision.bean.CoreDailySupervisionConBean;
import com.sunwayworld.cloud.module.qms.dailysupervision.persistent.dao.CoreDailySupervisionConDao;
import com.sunwayworld.cloud.module.qms.dailysupervision.service.CoreDailySupervisionConService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@GikamBean
public class CoreDailySupervisionConServiceImpl
implements CoreDailySupervisionConService {
    @Autowired
    private CoreDailySupervisionConDao coreDailySupervisionConDao;

    @Override
    public CoreDailySupervisionConDao getDao() {
        return this.coreDailySupervisionConDao;
    }

    @Override
    @Transactional
    @Audit(value="AUDIT.INSERT")
    @AuditTrailEntry(value=AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        CoreDailySupervisionConBean coreDailySupervisionCon = jsonWrapper.parseUnique(CoreDailySupervisionConBean.class);
        coreDailySupervisionCon.setId(ApplicationContextHelper.getNextIdentity());
        this.getDao().insert(coreDailySupervisionCon);
        return coreDailySupervisionCon.getId();
    }
}
