package com.sunwayworld.emc.module.mdm.basemdm.script.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.mdm.basemdm.script.bean.EmcScriptBean;
import com.sunwayworld.emc.module.mdm.basemdm.script.dao.EmcScriptDao;
import com.sunwayworld.emc.module.mdm.basemdm.script.service.EmcScriptService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;

@Repository
public class EmcScriptServiceImpl implements EmcScriptService {
    @Autowired
    private EmcScriptDao emcScriptDao;

    @Override
    @SuppressWarnings("unchecked")
    public EmcScriptDao getDao() {
        return emcScriptDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public String insert(RestJsonWrapperBean jsonWrapper) {
        EmcScriptBean emcScript = jsonWrapper.parseUnique(EmcScriptBean.class);
        emcScript.setId(ApplicationContextHelper.getNextSequence(ApplicationContextHelper.getNextSequence(getDao().getTable(), "CALC-")));
        getDao().insert(emcScript);
        return emcScript.getId();
    }

}
