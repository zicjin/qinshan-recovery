package com.sunwayworld.emc.module.item.scm.officemtl.service.impl;

import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.item.scm.officemtl.bean.EmcOfficeMtlBean;
import com.sunwayworld.emc.module.item.scm.officemtl.dao.EmcOfficeMtlDao;
import com.sunwayworld.emc.module.item.scm.officemtl.service.EmcOfficeMtlService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;

@Repository
@GikamBean
public class EmcOfficeMtlServiceImpl implements EmcOfficeMtlService {
    @Autowired
    private EmcOfficeMtlDao emcOfficeMtlDao;

    @Override
    @SuppressWarnings("unchecked")
    public EmcOfficeMtlDao getDao() {
        return emcOfficeMtlDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        EmcOfficeMtlBean emcOfficeMtl = jsonWrapper.parseUnique(EmcOfficeMtlBean.class);
        emcOfficeMtl.setId(ApplicationContextHelper.getNextIdentity());
        getDao().insert(emcOfficeMtl);
        return emcOfficeMtl.getId();
    }

}
