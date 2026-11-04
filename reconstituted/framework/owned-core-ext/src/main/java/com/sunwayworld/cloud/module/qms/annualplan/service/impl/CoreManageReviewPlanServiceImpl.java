/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.annualplan.service.impl;

import com.sunwayworld.cloud.module.qms.annualplan.bean.CoreManageReviewPlanBean;
import com.sunwayworld.cloud.module.qms.annualplan.persistent.dao.CoreManageReviewPlanDao;
import com.sunwayworld.cloud.module.qms.annualplan.service.CoreManageReviewPlanService;
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
public class CoreManageReviewPlanServiceImpl
implements CoreManageReviewPlanService {
    @Autowired
    private CoreManageReviewPlanDao coreManageReviewPlanDao;

    @Override
    public CoreManageReviewPlanDao getDao() {
        return this.coreManageReviewPlanDao;
    }

    @Override
    @Transactional
    @Audit(value="AUDIT.INSERT")
    @AuditTrailEntry(value=AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        CoreManageReviewPlanBean coreManageReviewPlan = jsonWrapper.parseUnique(CoreManageReviewPlanBean.class);
        coreManageReviewPlan.setId(ApplicationContextHelper.getNextIdentity());
        this.getDao().insert(coreManageReviewPlan);
        return coreManageReviewPlan.getId();
    }
}
