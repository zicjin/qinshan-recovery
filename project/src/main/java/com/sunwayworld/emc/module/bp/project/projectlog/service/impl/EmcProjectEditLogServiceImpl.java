package com.sunwayworld.emc.module.bp.project.projectlog.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.spring.annotation.GikamBean;

import com.sunwayworld.emc.module.bp.project.projectlog.dao.EmcProjectEditLogDao;
import com.sunwayworld.emc.module.bp.project.projectlog.bean.EmcProjectEditLogBean;
import com.sunwayworld.emc.module.bp.project.projectlog.service.EmcProjectEditLogService;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;

@Repository
@GikamBean
public class EmcProjectEditLogServiceImpl implements EmcProjectEditLogService {

    @Autowired
    private EmcProjectEditLogDao emcProjectEditLogDao;

    @Override
    @SuppressWarnings("unchecked")
    public EmcProjectEditLogDao getDao() {
        return emcProjectEditLogDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        EmcProjectEditLogBean emcProjectEditLog = jsonWrapper.parseUnique(EmcProjectEditLogBean.class);
        emcProjectEditLog.setId(ApplicationContextHelper.getNextIdentity());
        getDao().insert(emcProjectEditLog);
        return emcProjectEditLog.getId();
    }

}
