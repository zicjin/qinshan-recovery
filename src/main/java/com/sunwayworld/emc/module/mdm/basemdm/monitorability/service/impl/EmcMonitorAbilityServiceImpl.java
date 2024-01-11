package com.sunwayworld.emc.module.mdm.basemdm.monitorability.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.mdm.basemdm.monitorability.bean.EmcMonitorAbilityBean;
import com.sunwayworld.emc.module.mdm.basemdm.monitorability.dao.EmcMonitorAbilityDao;
import com.sunwayworld.emc.module.mdm.basemdm.monitorability.service.EmcMonitorAbilityService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;

/**
 * @author magt@sunwayworld.com
 * @date 2019-10-09
 */

@Repository
public class EmcMonitorAbilityServiceImpl implements EmcMonitorAbilityService {
    @Autowired
    private EmcMonitorAbilityDao emcMonitorAbilityDao;

    @Override
    @SuppressWarnings("unchecked")
    public EmcMonitorAbilityDao getDao() {
        return emcMonitorAbilityDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        EmcMonitorAbilityBean emcMonitorAbility = jsonWrapper.parseUnique(EmcMonitorAbilityBean.class);
        emcMonitorAbility.setId(ApplicationContextHelper.getNextIdentity());
        getDao().insert(emcMonitorAbility);
        return emcMonitorAbility.getId();
    }

}
