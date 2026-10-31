package com.sunwayworld.emc.module.mdm.basemdm.cert.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.mdm.basemdm.cert.bean.EmcCertLogBean;
import com.sunwayworld.emc.module.mdm.basemdm.cert.dao.EmcCertLogDao;
import com.sunwayworld.emc.module.mdm.basemdm.cert.service.EmcCertLogService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;

/**
 * @author zhangsy@sunwayworld.com
 * @date 2019-10-08
 */

@Repository
public class EmcCertLogServiceImpl implements EmcCertLogService {
    @Autowired
    private EmcCertLogDao emcCertLogDao;

    @Override
    @SuppressWarnings("unchecked")
    public EmcCertLogDao getDao() {
        return emcCertLogDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        EmcCertLogBean emcCertLog = jsonWrapper.parseUnique(EmcCertLogBean.class);
        emcCertLog.setId(ApplicationContextHelper.getNextIdentity());
        getDao().insert(emcCertLog);
        return emcCertLog.getId();
    }

}
