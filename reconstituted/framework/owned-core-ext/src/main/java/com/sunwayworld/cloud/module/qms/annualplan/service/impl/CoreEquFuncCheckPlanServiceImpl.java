/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.annualplan.service.impl;

import com.sunwayworld.cloud.module.qms.annualplan.bean.CoreEquFuncCheckPlanBean;
import com.sunwayworld.cloud.module.qms.annualplan.persistent.dao.CoreEquFuncCheckPlanDao;
import com.sunwayworld.cloud.module.qms.annualplan.service.CoreEquFuncCheckPlanService;
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
public class CoreEquFuncCheckPlanServiceImpl
implements CoreEquFuncCheckPlanService {
    @Autowired
    private CoreEquFuncCheckPlanDao coreEquFuncCheckPlanDao;

    @Override
    public CoreEquFuncCheckPlanDao getDao() {
        return this.coreEquFuncCheckPlanDao;
    }

    @Override
    @Transactional
    @Audit(value="AUDIT.INSERT")
    @AuditTrailEntry(value=AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        CoreEquFuncCheckPlanBean coreEquFuncCheckPlan = jsonWrapper.parseUnique(CoreEquFuncCheckPlanBean.class);
        coreEquFuncCheckPlan.setId(ApplicationContextHelper.getNextIdentity());
        this.getDao().insert(coreEquFuncCheckPlan);
        return coreEquFuncCheckPlan.getId();
    }

    @Override
    @Transactional
    @Audit(value="AUDIT.SAVE")
    @AuditTrailEntry(value=AuditTrailType.UPDATE)
    public void saveEquFuncCheckPlan(Long annualPlanId, RestJsonWrapperBean wrapper) {
        List<CoreEquFuncCheckPlanBean> coreEquFuncCheckPlanList = wrapper.parse(CoreEquFuncCheckPlanBean.class);
        ArrayList insertCoreEquFuncCheckPlanList = new ArrayList();
        ArrayList updateCoreEquFuncCheckPlanList = new ArrayList();
        coreEquFuncCheckPlanList.forEach(e -> {
            if (e.getId() == 0L) {
                e.setId(ApplicationContextHelper.getNextIdentity());
                e.setAnnualPlanId(annualPlanId);
                if (e.getEquId() == null || e.getEquId().isEmpty()) {
                    e.setEquId(ApplicationContextHelper.getNextIdentity().toString());
                }
                insertCoreEquFuncCheckPlanList.add(e);
            } else {
                updateCoreEquFuncCheckPlanList.add(e);
            }
        });
        if (!insertCoreEquFuncCheckPlanList.isEmpty()) {
            this.getDao().insert(insertCoreEquFuncCheckPlanList);
        }
        if (!updateCoreEquFuncCheckPlanList.isEmpty()) {
            this.getDao().update(updateCoreEquFuncCheckPlanList, new String[0]);
        }
    }
}
