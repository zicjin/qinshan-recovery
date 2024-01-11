package com.sunwayworld.emc.module.mdm.basemdm.sealrule.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.spring.annotation.GikamBean;

import com.sunwayworld.emc.module.mdm.basemdm.sealrule.dao.EmcSealRuleDetailsDao;
import com.sunwayworld.emc.module.mdm.basemdm.sealrule.bean.EmcSealRuleDetailsBean;
import com.sunwayworld.emc.module.mdm.basemdm.sealrule.service.EmcSealRuleDetailsService;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;

@Repository
@GikamBean
public class EmcSealRuleDetailsServiceImpl implements EmcSealRuleDetailsService {

    @Autowired
    private EmcSealRuleDetailsDao emcSealRuleDetailsDao;

    @Override
    @SuppressWarnings("unchecked")
    public EmcSealRuleDetailsDao getDao() {
        return emcSealRuleDetailsDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        EmcSealRuleDetailsBean emcSealRuleDetails = jsonWrapper.parseUnique(EmcSealRuleDetailsBean.class);
        emcSealRuleDetails.setId(ApplicationContextHelper.getNextIdentity());
        getDao().insert(emcSealRuleDetails);
        return emcSealRuleDetails.getId();
    }

}
