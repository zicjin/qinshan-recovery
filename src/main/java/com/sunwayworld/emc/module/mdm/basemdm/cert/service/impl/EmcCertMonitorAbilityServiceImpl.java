package com.sunwayworld.emc.module.mdm.basemdm.cert.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.mdm.basemdm.cert.bean.EmcCertMonitorAbilityBean;
import com.sunwayworld.emc.module.mdm.basemdm.cert.dao.EmcCertMonitorAbilityDao;
import com.sunwayworld.emc.module.mdm.basemdm.cert.service.EmcCertMonitorAbilityService;
import com.sunwayworld.emc.module.mdm.basemdm.monitorability.bean.EmcMonitorAbilityBean;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;

/**
 * @author zhangsy@sunwayworld.com
 * @date 2019-10-08
 */

@Repository
public class EmcCertMonitorAbilityServiceImpl implements EmcCertMonitorAbilityService {
    @Autowired
    private EmcCertMonitorAbilityDao emcCertMonitorAbilityDao;

    @Override
    @SuppressWarnings("unchecked")
    public EmcCertMonitorAbilityDao getDao() {
        return emcCertMonitorAbilityDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public void insert(Long certId, RestJsonWrapperBean jsonWrapper) {
        List<EmcMonitorAbilityBean> monitorAbilityList = jsonWrapper.parse(EmcMonitorAbilityBean.class);

        List<EmcCertMonitorAbilityBean> certMonitorAbilityList = new ArrayList<EmcCertMonitorAbilityBean>();
        monitorAbilityList.stream().forEach(e -> {
            EmcCertMonitorAbilityBean emcCertMonitorAbility = new EmcCertMonitorAbilityBean();
            emcCertMonitorAbility.setId(ApplicationContextHelper.getNextIdentity());
            emcCertMonitorAbility.setCertId(certId);
            emcCertMonitorAbility.setMonotorAbilityId(e.getId());
            certMonitorAbilityList.add(emcCertMonitorAbility);
        });

        getDao().insert(certMonitorAbilityList);
    }

}
