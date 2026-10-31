package com.sunwayworld.emc.module.mdm.bizmdm.test.service.impl;

import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcItemResultBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.spring.annotation.GikamBean;

import com.sunwayworld.emc.module.mdm.bizmdm.test.dao.EmcTestmethodBeforehandleDao;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcTestmethodBeforehandleBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.EmcTestmethodBeforehandleService;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;

@Repository
@GikamBean
public class EmcTestmethodBeforehandleServiceImpl implements EmcTestmethodBeforehandleService {

    @Autowired
    private EmcTestmethodBeforehandleDao emcTestmethodBeforehandleDao;

    @Override
    @SuppressWarnings("unchecked")
    public EmcTestmethodBeforehandleDao getDao() {
        return emcTestmethodBeforehandleDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(Long testMethodId, RestJsonWrapperBean jsonWrapper) {
        EmcTestmethodBeforehandleBean emcTestmethodBeforehandle = jsonWrapper.parseUnique(EmcTestmethodBeforehandleBean.class);
        emcTestmethodBeforehandle.setId(ApplicationContextHelper.getNextIdentity());
        emcTestmethodBeforehandle.setTestMethodId(testMethodId);
        getDao().insert(emcTestmethodBeforehandle);
        return emcTestmethodBeforehandle.getId();
    }

}
