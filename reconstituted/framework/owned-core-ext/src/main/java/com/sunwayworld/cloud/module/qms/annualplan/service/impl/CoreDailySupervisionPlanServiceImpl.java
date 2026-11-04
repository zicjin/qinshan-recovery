/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.annualplan.service.impl;

import com.sunwayworld.cloud.module.qms.annualplan.bean.CoreDailySupervisionPlanBean;
import com.sunwayworld.cloud.module.qms.annualplan.persistent.dao.CoreDailySupervisionPlanDao;
import com.sunwayworld.cloud.module.qms.annualplan.service.CoreDailySupervisionPlanService;
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
public class CoreDailySupervisionPlanServiceImpl
implements CoreDailySupervisionPlanService {
    @Autowired
    private CoreDailySupervisionPlanDao coreDailySupervisionPlanDao;

    @Override
    public CoreDailySupervisionPlanDao getDao() {
        return this.coreDailySupervisionPlanDao;
    }

    @Override
    @Transactional
    @Audit(value="AUDIT.INSERT")
    @AuditTrailEntry(value=AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        CoreDailySupervisionPlanBean coreDailySupervisionPlan = jsonWrapper.parseUnique(CoreDailySupervisionPlanBean.class);
        coreDailySupervisionPlan.setId(ApplicationContextHelper.getNextIdentity());
        this.getDao().insert(coreDailySupervisionPlan);
        return coreDailySupervisionPlan.getId();
    }
}
