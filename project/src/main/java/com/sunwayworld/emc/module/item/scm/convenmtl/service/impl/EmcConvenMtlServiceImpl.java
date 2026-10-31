package com.sunwayworld.emc.module.item.scm.convenmtl.service.impl;

import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.item.scm.convenmtl.bean.EmcConvenMtlBean;
import com.sunwayworld.emc.module.item.scm.convenmtl.dao.EmcConvenMtlDao;
import com.sunwayworld.emc.module.item.scm.convenmtl.service.EmcConvenMtlService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;


@Repository
@GikamBean
public class EmcConvenMtlServiceImpl implements EmcConvenMtlService {
    @Autowired
    private EmcConvenMtlDao emcConvenMtlDao;

    @Override
    @SuppressWarnings("unchecked")
    public EmcConvenMtlDao getDao() {
        return emcConvenMtlDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        EmcConvenMtlBean emcConvenMtl = jsonWrapper.parseUnique(EmcConvenMtlBean.class);
        emcConvenMtl.setId(ApplicationContextHelper.getNextIdentity());
        getDao().insert(emcConvenMtl);
        return emcConvenMtl.getId();
    }

}
