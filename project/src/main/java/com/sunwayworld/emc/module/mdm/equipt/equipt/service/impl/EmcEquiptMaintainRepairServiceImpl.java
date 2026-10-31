package com.sunwayworld.emc.module.mdm.equipt.equipt.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.spring.annotation.GikamBean;

import com.sunwayworld.emc.module.mdm.equipt.equipt.dao.EmcEquiptMaintainRepairDao;
import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.EmcEquiptMaintainRepairBean;
import com.sunwayworld.emc.module.mdm.equipt.equipt.service.EmcEquiptMaintainRepairService;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;

@Repository
@GikamBean
public class EmcEquiptMaintainRepairServiceImpl implements EmcEquiptMaintainRepairService {

    @Autowired
    private EmcEquiptMaintainRepairDao emcEquiptMaintainRepairDao;

    @Override
    @SuppressWarnings("unchecked")
    public EmcEquiptMaintainRepairDao getDao() {
        return emcEquiptMaintainRepairDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        EmcEquiptMaintainRepairBean emcEquiptMaintainRepair = jsonWrapper.parseUnique(EmcEquiptMaintainRepairBean.class);
        emcEquiptMaintainRepair.setId(ApplicationContextHelper.getNextIdentity());
        getDao().insert(emcEquiptMaintainRepair);
        return emcEquiptMaintainRepair.getId();
    }

}
