package com.sunwayworld.emc.module.item.mtlpercheck.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.spring.annotation.GikamBean;

import com.sunwayworld.emc.module.item.mtlpercheck.dao.EmcMtlPerCheckDao;
import com.sunwayworld.emc.module.item.mtlpercheck.bean.EmcMtlPerCheckBean;
import com.sunwayworld.emc.module.item.mtlpercheck.service.EmcMtlPerCheckService;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;

@Repository
@GikamBean
public class EmcMtlPerCheckServiceImpl implements EmcMtlPerCheckService {

    @Autowired
    private EmcMtlPerCheckDao emcMtlPerCheckDao;

    @Override
    @SuppressWarnings("unchecked")
    public EmcMtlPerCheckDao getDao() {
        return emcMtlPerCheckDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        EmcMtlPerCheckBean emcMtlPerCheck = jsonWrapper.parseUnique(EmcMtlPerCheckBean.class);
        emcMtlPerCheck.setId(ApplicationContextHelper.getNextIdentity());
        getDao().insert(emcMtlPerCheck);
        return emcMtlPerCheck.getId();
    }

}
