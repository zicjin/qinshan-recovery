package com.sunwayworld.emc.module.item.equiptassayplan.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.spring.annotation.GikamBean;

import com.sunwayworld.emc.module.item.equiptassayplan.dao.EmcEquiptAssayPlanDao;
import com.sunwayworld.emc.module.item.equiptassayplan.bean.EmcEquiptAssayPlanBean;
import com.sunwayworld.emc.module.item.equiptassayplan.service.EmcEquiptAssayPlanService;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;

@Repository
@GikamBean
public class EmcEquiptAssayPlanServiceImpl implements EmcEquiptAssayPlanService {

    @Autowired
    private EmcEquiptAssayPlanDao emcEquiptAssayPlanDao;

    @Override
    @SuppressWarnings("unchecked")
    public EmcEquiptAssayPlanDao getDao() {
        return emcEquiptAssayPlanDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        EmcEquiptAssayPlanBean emcEquiptAssayPlan = jsonWrapper.parseUnique(EmcEquiptAssayPlanBean.class);
        emcEquiptAssayPlan.setId(ApplicationContextHelper.getNextIdentity());
        getDao().insert(emcEquiptAssayPlan);
        return emcEquiptAssayPlan.getId();
    }

}
