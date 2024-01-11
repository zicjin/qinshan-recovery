package com.sunwayworld.emc.module.mdm.equipt.equiptacqscript.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.mdm.equipt.equiptacqscript.bean.EmcEasPreAnalyBean;
import com.sunwayworld.emc.module.mdm.equipt.equiptacqscript.dao.EmcEasPreAnalyDao;
import com.sunwayworld.emc.module.mdm.equipt.equiptacqscript.service.EmcEasPreAnalyService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@Repository
@GikamBean
public class EmcEasPreAnalyServiceImpl implements EmcEasPreAnalyService {

    @Autowired
    private EmcEasPreAnalyDao emcEasPreAnalyDao;

    @SuppressWarnings("unchecked")
    @Override
    public EmcEasPreAnalyDao getDao() {
        return emcEasPreAnalyDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(Long scriptId) {
        EmcEasPreAnalyBean easPreAnaly = new EmcEasPreAnalyBean();
        easPreAnaly.setId(ApplicationContextHelper.getNextIdentity());
        easPreAnaly.setScriptId(scriptId);
        getDao().insert(easPreAnaly);
        return easPreAnaly.getId();
    }

}
