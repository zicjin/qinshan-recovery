package com.sunwayworld.emc.module.mdm.bizmdm.samplenumber.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.spring.annotation.GikamBean;

import com.sunwayworld.emc.module.mdm.bizmdm.samplenumber.dao.EmcSamplenumberDao;
import com.sunwayworld.emc.module.mdm.bizmdm.samplenumber.bean.EmcSamplenumberBean;
import com.sunwayworld.emc.module.mdm.bizmdm.samplenumber.service.EmcSamplenumberService;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;

@Repository
@GikamBean
public class EmcSamplenumberServiceImpl implements EmcSamplenumberService {

    @Autowired
    private EmcSamplenumberDao emcSamplenumberDao;

    @Override
    @SuppressWarnings("unchecked")
    public EmcSamplenumberDao getDao() {
        return emcSamplenumberDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        EmcSamplenumberBean emcSamplenumber = jsonWrapper.parseUnique(EmcSamplenumberBean.class);
        emcSamplenumber.setId(ApplicationContextHelper.getNextIdentity());
        getDao().insert(emcSamplenumber);
        return emcSamplenumber.getId();
    }

}
