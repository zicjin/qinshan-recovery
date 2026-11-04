/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.annualplan.service.impl;

import com.sunwayworld.cloud.module.qms.annualplan.bean.CoreQualityControlPlanBean;
import com.sunwayworld.cloud.module.qms.annualplan.persistent.dao.CoreQualityControlPlanDao;
import com.sunwayworld.cloud.module.qms.annualplan.service.CoreQualityControlPlanService;
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
public class CoreQualityControlPlanServiceImpl
implements CoreQualityControlPlanService {
    @Autowired
    private CoreQualityControlPlanDao coreQualityControlPlanDao;

    @Override
    public CoreQualityControlPlanDao getDao() {
        return this.coreQualityControlPlanDao;
    }

    @Override
    @Transactional
    @Audit(value="AUDIT.INSERT")
    @AuditTrailEntry(value=AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        CoreQualityControlPlanBean coreQualityControlPlan = jsonWrapper.parseUnique(CoreQualityControlPlanBean.class);
        coreQualityControlPlan.setId(ApplicationContextHelper.getNextIdentity());
        this.getDao().insert(coreQualityControlPlan);
        return coreQualityControlPlan.getId();
    }
}
