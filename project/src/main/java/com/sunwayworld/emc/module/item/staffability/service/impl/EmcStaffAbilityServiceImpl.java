package com.sunwayworld.emc.module.item.staffability.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.spring.annotation.GikamBean;

import com.sunwayworld.emc.module.item.staffability.dao.EmcStaffAbilityDao;
import com.sunwayworld.emc.module.item.staffability.bean.EmcStaffAbilityBean;
import com.sunwayworld.emc.module.item.staffability.service.EmcStaffAbilityService;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;

@Repository
@GikamBean
public class EmcStaffAbilityServiceImpl implements EmcStaffAbilityService {

    @Autowired
    private EmcStaffAbilityDao emcStaffAbilityDao;

    @Override
    @SuppressWarnings("unchecked")
    public EmcStaffAbilityDao getDao() {
        return emcStaffAbilityDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        EmcStaffAbilityBean emcStaffAbility = jsonWrapper.parseUnique(EmcStaffAbilityBean.class);
        emcStaffAbility.setId(ApplicationContextHelper.getNextIdentity());
        getDao().insert(emcStaffAbility);
        return emcStaffAbility.getId();
    }

}
