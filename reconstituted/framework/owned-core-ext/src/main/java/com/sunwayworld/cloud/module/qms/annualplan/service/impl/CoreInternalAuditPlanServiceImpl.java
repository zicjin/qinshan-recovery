/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.annualplan.service.impl;

import com.sunwayworld.cloud.module.qms.annualplan.bean.CoreInternalAuditPlanBean;
import com.sunwayworld.cloud.module.qms.annualplan.persistent.dao.CoreInternalAuditPlanDao;
import com.sunwayworld.cloud.module.qms.annualplan.service.CoreInternalAuditPlanService;
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
public class CoreInternalAuditPlanServiceImpl
implements CoreInternalAuditPlanService {
    @Autowired
    private CoreInternalAuditPlanDao coreInternalAuditPlanDao;

    @Override
    public CoreInternalAuditPlanDao getDao() {
        return this.coreInternalAuditPlanDao;
    }

    @Override
    @Transactional
    @Audit(value="AUDIT.INSERT")
    @AuditTrailEntry(value=AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        CoreInternalAuditPlanBean coreInternalAuditPlan = jsonWrapper.parseUnique(CoreInternalAuditPlanBean.class);
        coreInternalAuditPlan.setId(ApplicationContextHelper.getNextIdentity());
        this.getDao().insert(coreInternalAuditPlan);
        return coreInternalAuditPlan.getId();
    }
}
