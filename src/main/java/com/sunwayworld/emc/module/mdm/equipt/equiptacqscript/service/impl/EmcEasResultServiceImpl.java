package com.sunwayworld.emc.module.mdm.equipt.equiptacqscript.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.mdm.equipt.equiptacqscript.bean.EmcEasResultBean;
import com.sunwayworld.emc.module.mdm.equipt.equiptacqscript.dao.EmcEasResultDao;
import com.sunwayworld.emc.module.mdm.equipt.equiptacqscript.service.EmcEasResultService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@Repository
@GikamBean
public class EmcEasResultServiceImpl implements EmcEasResultService {

    @Autowired
    private EmcEasResultDao emcEasResultDao;

    @SuppressWarnings("unchecked")
    @Override
    public EmcEasResultDao getDao() {
        return emcEasResultDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(Long scriptId) {
        EmcEasResultBean easResult = new EmcEasResultBean();
        easResult.setId(ApplicationContextHelper.getNextIdentity());
        easResult.setScriptId(scriptId);
        getDao().insert(easResult);
        return easResult.getId();
    }

}
