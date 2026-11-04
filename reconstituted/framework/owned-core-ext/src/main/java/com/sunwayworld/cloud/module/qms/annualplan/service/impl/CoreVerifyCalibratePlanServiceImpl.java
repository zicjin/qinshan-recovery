/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.annualplan.service.impl;

import com.sunwayworld.cloud.module.qms.annualplan.bean.CoreVerifyCalibratePlanBean;
import com.sunwayworld.cloud.module.qms.annualplan.persistent.dao.CoreVerifyCalibratePlanDao;
import com.sunwayworld.cloud.module.qms.annualplan.service.CoreVerifyCalibratePlanService;
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
public class CoreVerifyCalibratePlanServiceImpl
implements CoreVerifyCalibratePlanService {
    @Autowired
    private CoreVerifyCalibratePlanDao coreVerifyCalibratePlanDao;

    @Override
    public CoreVerifyCalibratePlanDao getDao() {
        return this.coreVerifyCalibratePlanDao;
    }

    @Override
    @Transactional
    @Audit(value="AUDIT.SAVE")
    @AuditTrailEntry(value=AuditTrailType.UPDATE)
    public void saveVerifyCalibratePlan(Long annualPlanId, RestJsonWrapperBean wrapper) {
        List<CoreVerifyCalibratePlanBean> coreVerifyCalibratePlanList = wrapper.parse(CoreVerifyCalibratePlanBean.class);
        ArrayList insertCoreVerifyCalibratePlanList = new ArrayList();
        ArrayList updateCoreVerifyCalibratePlanList = new ArrayList();
        coreVerifyCalibratePlanList.forEach(e -> {
            if (e.getId() == 0L) {
                e.setId(ApplicationContextHelper.getNextIdentity());
                e.setAnnualPlanId(annualPlanId);
                if (e.getEquId() == null || e.getEquId().isEmpty()) {
                    e.setEquId(ApplicationContextHelper.getNextIdentity().toString());
                }
                insertCoreVerifyCalibratePlanList.add(e);
            } else {
                updateCoreVerifyCalibratePlanList.add(e);
            }
        });
        if (!insertCoreVerifyCalibratePlanList.isEmpty()) {
            this.getDao().insert(insertCoreVerifyCalibratePlanList);
        }
        if (!updateCoreVerifyCalibratePlanList.isEmpty()) {
            this.getDao().update(updateCoreVerifyCalibratePlanList, new String[0]);
        }
    }
}
