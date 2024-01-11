package com.sunwayworld.emc.module.mdm.filemodification.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.mdm.filemodification.bean.EmcFileModificationBean;
import com.sunwayworld.emc.module.mdm.filemodification.dao.EmcFileModificationDao;
import com.sunwayworld.emc.module.mdm.filemodification.service.EmcFileModificationService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author zhanghy@sunwayworld.com 2020-09-01
 */
@Repository
@GikamBean
public class EmcFileModificationServiceImpl implements EmcFileModificationService {

    @Autowired
    private EmcFileModificationDao emcFileModificationDao;
    
    @SuppressWarnings("unchecked")
    @Override
    public EmcFileModificationDao getDao() {
        return emcFileModificationDao;
    }
    
    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean wrapper) {
        EmcFileModificationBean emcFileModification = wrapper.parseUnique(EmcFileModificationBean.class);
        emcFileModification.setId(ApplicationContextHelper.getNextIdentity());
        getDao().insert(emcFileModification);

        return emcFileModification.getId();
    }
}
