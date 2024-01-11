package com.sunwayworld.emc.module.item.staffabilityevaluation.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.spring.annotation.GikamBean;

import com.sunwayworld.emc.module.item.staffabilityevaluation.dao.EmcStaffAbilityEvaluationDao;
import com.sunwayworld.emc.module.item.staffabilityevaluation.bean.EmcStaffAbilityEvaluationBean;
import com.sunwayworld.emc.module.item.staffabilityevaluation.service.EmcStaffAbilityEvaluationService;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;

@Repository
@GikamBean
public class EmcStaffAbilityEvaluationServiceImpl implements EmcStaffAbilityEvaluationService {

    @Autowired
    private EmcStaffAbilityEvaluationDao emcStaffAbilityEvaluationDao;

    @Override
    @SuppressWarnings("unchecked")
    public EmcStaffAbilityEvaluationDao getDao() {
        return emcStaffAbilityEvaluationDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        EmcStaffAbilityEvaluationBean emcStaffAbilityEvaluation = jsonWrapper.parseUnique(EmcStaffAbilityEvaluationBean.class);
        emcStaffAbilityEvaluation.setId(ApplicationContextHelper.getNextIdentity());
        getDao().insert(emcStaffAbilityEvaluation);
        return emcStaffAbilityEvaluation.getId();
    }

}
