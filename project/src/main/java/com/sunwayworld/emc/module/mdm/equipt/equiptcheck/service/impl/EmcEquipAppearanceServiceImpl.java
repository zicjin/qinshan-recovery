package com.sunwayworld.emc.module.mdm.equipt.equiptcheck.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.mdm.equipt.equiptcheck.bean.EmcEquipAppearanceBean;
import com.sunwayworld.emc.module.mdm.equipt.equiptcheck.dao.EmcEquipAppearanceDao;
import com.sunwayworld.emc.module.mdm.equipt.equiptcheck.service.EmcEquipAppearanceService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@Repository
@GikamBean
public class EmcEquipAppearanceServiceImpl implements EmcEquipAppearanceService {

    @Autowired
    private EmcEquipAppearanceDao emcEquipAppearanceDao;

    @Override
    @SuppressWarnings("unchecked")
    public EmcEquipAppearanceDao getDao() {
        return emcEquipAppearanceDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        EmcEquipAppearanceBean emcEquipAppearance = jsonWrapper.parseUnique(EmcEquipAppearanceBean.class);
        emcEquipAppearance.setId(ApplicationContextHelper.getNextIdentity());
        getDao().insert(emcEquipAppearance);
        return emcEquipAppearance.getId();
    }

}
