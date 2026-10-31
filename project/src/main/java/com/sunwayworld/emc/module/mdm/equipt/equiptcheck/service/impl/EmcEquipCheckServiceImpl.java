package com.sunwayworld.emc.module.mdm.equipt.equiptcheck.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.mdm.equipt.equiptcheck.bean.EmcEquipCheckBean;
import com.sunwayworld.emc.module.mdm.equipt.equiptcheck.dao.EmcEquipCheckDao;
import com.sunwayworld.emc.module.mdm.equipt.equiptcheck.service.EmcEquipCheckService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@Repository
@GikamBean
public class EmcEquipCheckServiceImpl implements EmcEquipCheckService {

    @Autowired
    private EmcEquipCheckDao emcEquipCheckDao;

    @Override
    @SuppressWarnings("unchecked")
    public EmcEquipCheckDao getDao() {
        return emcEquipCheckDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        EmcEquipCheckBean emcEquipCheck = jsonWrapper.parseUnique(EmcEquipCheckBean.class);
        emcEquipCheck.setId(ApplicationContextHelper.getNextIdentity());
        getDao().insert(emcEquipCheck);
        return emcEquipCheck.getId();
    }
}
