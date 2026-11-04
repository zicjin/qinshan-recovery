/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.annualplan.service.impl;

import com.sunwayworld.cloud.module.qms.annualplan.bean.CorePeriodVerifyPlanBean;
import com.sunwayworld.cloud.module.qms.annualplan.persistent.dao.CorePeriodVerifyPlanDao;
import com.sunwayworld.cloud.module.qms.annualplan.service.CorePeriodVerifyPlanService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@GikamBean
public class CorePeriodVerifyPlanServiceImpl
implements CorePeriodVerifyPlanService {
    @Autowired
    private CorePeriodVerifyPlanDao corePeriodVerifyPlanDao;

    @Override
    public CorePeriodVerifyPlanDao getDao() {
        return this.corePeriodVerifyPlanDao;
    }

    @Override
    @Transactional
    @Audit(value="AUDIT.SAVE")
    @AuditTrailEntry(value=AuditTrailType.UPDATE)
    public void savePeriodVerifyPlan(Long annualPlanId, RestJsonWrapperBean wrapper) {
        List<CorePeriodVerifyPlanBean> corePeriodVerifyPlanList = wrapper.parse(CorePeriodVerifyPlanBean.class);
        ArrayList insertCorePeriodVerifyPlanList = new ArrayList();
        ArrayList updateCorePeriodVerifyPlanList = new ArrayList();
        corePeriodVerifyPlanList.forEach(e -> {
            if (e.getId() == 0L) {
                e.setId(ApplicationContextHelper.getNextIdentity());
                e.setAnnualPlanId(annualPlanId);
                if (e.getEquId() == null || e.getEquId().isEmpty()) {
                    e.setEquId(ApplicationContextHelper.getNextIdentity().toString());
                }
                insertCorePeriodVerifyPlanList.add(e);
            } else {
                updateCorePeriodVerifyPlanList.add(e);
            }
        });
        if (!insertCorePeriodVerifyPlanList.isEmpty()) {
            this.getDao().insert(insertCorePeriodVerifyPlanList);
        }
        if (!updateCorePeriodVerifyPlanList.isEmpty()) {
            this.getDao().update(updateCorePeriodVerifyPlanList, new String[0]);
        }
    }
}
