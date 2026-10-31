package com.sunwayworld.emc.module.item.yeartrainplan.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.spring.annotation.GikamBean;

import com.sunwayworld.emc.module.item.yeartrainplan.dao.EmcYearTrainPlanDao;
import com.sunwayworld.emc.module.item.yeartrainplan.bean.EmcYearTrainPlanBean;
import com.sunwayworld.emc.module.item.yeartrainplan.service.EmcYearTrainPlanService;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;

@Repository
@GikamBean
public class EmcYearTrainPlanServiceImpl implements EmcYearTrainPlanService {

    @Autowired
    private EmcYearTrainPlanDao emcYearTrainPlanDao;

    @Override
    @SuppressWarnings("unchecked")
    public EmcYearTrainPlanDao getDao() {
        return emcYearTrainPlanDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        EmcYearTrainPlanBean emcYearTrainPlan = jsonWrapper.parseUnique(EmcYearTrainPlanBean.class);
        emcYearTrainPlan.setId(ApplicationContextHelper.getNextIdentity());
        getDao().insert(emcYearTrainPlan);
        return emcYearTrainPlan.getId();
    }

}
