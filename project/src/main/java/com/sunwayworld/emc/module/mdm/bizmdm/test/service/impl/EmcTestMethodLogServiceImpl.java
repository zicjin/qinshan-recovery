package com.sunwayworld.emc.module.mdm.bizmdm.test.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.spring.annotation.GikamBean;

import com.sunwayworld.emc.module.mdm.bizmdm.test.dao.EmcTestMethodLogDao;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcTestMethodLogBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.EmcTestMethodLogService;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;

@Repository
@GikamBean
public class EmcTestMethodLogServiceImpl implements EmcTestMethodLogService {

    @Autowired
    private EmcTestMethodLogDao emcTestMethodLogDao;

    @Override
    @SuppressWarnings("unchecked")
    public EmcTestMethodLogDao getDao() {
        return emcTestMethodLogDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        EmcTestMethodLogBean emcTestMethodLog = jsonWrapper.parseUnique(EmcTestMethodLogBean.class);
        emcTestMethodLog.setId(ApplicationContextHelper.getNextIdentity());
        getDao().insert(emcTestMethodLog);
        return emcTestMethodLog.getId();
    }

}
