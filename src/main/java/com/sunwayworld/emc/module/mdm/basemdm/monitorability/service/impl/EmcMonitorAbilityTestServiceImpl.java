package com.sunwayworld.emc.module.mdm.basemdm.monitorability.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.mdm.basemdm.monitorability.bean.EmcMonitorAbilityTestBean;
import com.sunwayworld.emc.module.mdm.basemdm.monitorability.dao.EmcMonitorAbilityTestDao;
import com.sunwayworld.emc.module.mdm.basemdm.monitorability.service.EmcMonitorAbilityTestService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;

/**
 * @author magt@sunwayworld.com
 * @date 2019-10-09
 */

@Repository
public class EmcMonitorAbilityTestServiceImpl implements EmcMonitorAbilityTestService {
    @Autowired
    private EmcMonitorAbilityTestDao emcMonitorAbilityTestDao;

    @Override
    @SuppressWarnings("unchecked")
    public EmcMonitorAbilityTestDao getDao() {
        return emcMonitorAbilityTestDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(Long moniAbilId, RestJsonWrapperBean jsonWrapper) {
        List<EmcMonitorAbilityTestBean> monitorAbilityTestList = jsonWrapper.parse(EmcMonitorAbilityTestBean.class);
        monitorAbilityTestList.stream().forEach(e -> {
            e.setId(ApplicationContextHelper.getNextIdentity());
        });
        getDao().insert(monitorAbilityTestList);
        return moniAbilId;
    }

}
