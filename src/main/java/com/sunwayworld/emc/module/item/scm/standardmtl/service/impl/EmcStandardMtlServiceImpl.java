package com.sunwayworld.emc.module.item.scm.standardmtl.service.impl;

import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.item.scm.standardmtl.bean.EmcStandardMtlBean;
import com.sunwayworld.emc.module.item.scm.standardmtl.dao.EmcStandardMtlDao;
import com.sunwayworld.emc.module.item.scm.standardmtl.service.EmcStandardMtlService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;


@Repository
@GikamBean
public class EmcStandardMtlServiceImpl implements EmcStandardMtlService {
    @Autowired
    private EmcStandardMtlDao emcStandardMtlDao;

    @Override
    @SuppressWarnings("unchecked")
    public EmcStandardMtlDao getDao() {
        return emcStandardMtlDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        EmcStandardMtlBean emcStandardMtl = jsonWrapper.parseUnique(EmcStandardMtlBean.class);
        emcStandardMtl.setId(ApplicationContextHelper.getNextIdentity());
        getDao().insert(emcStandardMtl);
        return emcStandardMtl.getId();
    }

}
