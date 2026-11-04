/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.annualplan.service.impl;

import com.sunwayworld.cloud.module.qms.annualplan.bean.CoreEmpTrainPlanBean;
import com.sunwayworld.cloud.module.qms.annualplan.persistent.dao.CoreEmpTrainPlanDao;
import com.sunwayworld.cloud.module.qms.annualplan.service.CoreEmpTrainPlanService;
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
public class CoreEmpTrainPlanServiceImpl
implements CoreEmpTrainPlanService {
    @Autowired
    private CoreEmpTrainPlanDao coreEmpTrainPlanDao;

    @Override
    public CoreEmpTrainPlanDao getDao() {
        return this.coreEmpTrainPlanDao;
    }

    @Override
    @Transactional
    @Audit(value="AUDIT.SAVE")
    @AuditTrailEntry(value=AuditTrailType.UPDATE)
    public void saveEmpTrainPlan(Long annualPlanId, RestJsonWrapperBean wrapper) {
        List<CoreEmpTrainPlanBean> coreEmpTrainPlanList = wrapper.parse(CoreEmpTrainPlanBean.class);
        ArrayList insertCoreEmpTrainPlanList = new ArrayList();
        ArrayList updateCoreEmpTrainPlanList = new ArrayList();
        coreEmpTrainPlanList.forEach(e -> {
            if (e.getId() == 0L) {
                e.setId(ApplicationContextHelper.getNextIdentity());
                e.setAnnualPlanId(annualPlanId);
                insertCoreEmpTrainPlanList.add(e);
            } else {
                updateCoreEmpTrainPlanList.add(e);
            }
        });
        if (!insertCoreEmpTrainPlanList.isEmpty()) {
            this.getDao().insert(insertCoreEmpTrainPlanList);
        }
        if (!updateCoreEmpTrainPlanList.isEmpty()) {
            this.getDao().update(updateCoreEmpTrainPlanList, new String[0]);
        }
    }
}
