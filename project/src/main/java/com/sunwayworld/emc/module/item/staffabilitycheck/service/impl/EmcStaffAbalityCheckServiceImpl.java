package com.sunwayworld.emc.module.item.staffabilitycheck.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.spring.annotation.GikamBean;

import com.sunwayworld.emc.module.item.staffabilitycheck.dao.EmcStaffAbalityCheckDao;
import com.sunwayworld.emc.module.item.staffabilitycheck.bean.EmcStaffAbalityCheckBean;
import com.sunwayworld.emc.module.item.staffabilitycheck.service.EmcStaffAbalityCheckService;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;

@Repository
@GikamBean
public class EmcStaffAbalityCheckServiceImpl implements EmcStaffAbalityCheckService {

    @Autowired
    private EmcStaffAbalityCheckDao emcStaffAbalityCheckDao;

    @Override
    @SuppressWarnings("unchecked")
    public EmcStaffAbalityCheckDao getDao() {
        return emcStaffAbalityCheckDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        EmcStaffAbalityCheckBean emcStaffAbalityCheck = jsonWrapper.parseUnique(EmcStaffAbalityCheckBean.class);
        emcStaffAbalityCheck.setId(ApplicationContextHelper.getNextIdentity());
        getDao().insert(emcStaffAbalityCheck);
        return emcStaffAbalityCheck.getId();
    }

}
