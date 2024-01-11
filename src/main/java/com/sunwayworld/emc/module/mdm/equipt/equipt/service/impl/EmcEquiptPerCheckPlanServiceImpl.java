package com.sunwayworld.emc.module.mdm.equipt.equipt.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.EmcEquiptPerCheckPlanBean;
import com.sunwayworld.emc.module.mdm.equipt.equipt.dao.EmcEquiptPerCheckPlanDao;
import com.sunwayworld.emc.module.mdm.equipt.equipt.service.EmcEquiptPerCheckPlanService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;

/**
 * @author liuyz@sunwayworld.com 2020年09月25日
 */
@Repository
public class EmcEquiptPerCheckPlanServiceImpl implements EmcEquiptPerCheckPlanService {

    @Autowired
    private EmcEquiptPerCheckPlanDao emcEquiptPerCheckPlanDao;

    @Override
    @SuppressWarnings("unchecked")
    public EmcEquiptPerCheckPlanDao getDao() {
        return emcEquiptPerCheckPlanDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        EmcEquiptPerCheckPlanBean emcEquiptPlanBean = jsonWrapper.parseUnique(EmcEquiptPerCheckPlanBean.class);
        emcEquiptPlanBean.setId(ApplicationContextHelper.getNextIdentity());
        getDao().insert(emcEquiptPlanBean);
        return emcEquiptPlanBean.getId();
    }

    @Override
    public void updatePlan(EmcEquiptPerCheckPlanBean emcEquiptPerCheckPlanBean) {
        getDao().updatePlan(emcEquiptPerCheckPlanBean);
    }
}
