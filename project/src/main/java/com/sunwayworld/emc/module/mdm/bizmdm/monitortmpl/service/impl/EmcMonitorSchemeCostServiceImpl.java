package com.sunwayworld.emc.module.mdm.bizmdm.monitortmpl.service.impl;

import com.sunwayworld.emc.module.mdm.bizmdm.monitortmpl.bean.EmcMonitorSchemeCostBean;
import com.sunwayworld.emc.module.mdm.bizmdm.monitortmpl.dao.EmcMonitorSchemeCostDao;
import com.sunwayworld.emc.module.mdm.bizmdm.monitortmpl.service.EmcMonitorSchemeCostService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@GikamBean
public class EmcMonitorSchemeCostServiceImpl implements EmcMonitorSchemeCostService {
    @Autowired
    private EmcMonitorSchemeCostDao monitorSchemeCostDao;

    @Override
    @SuppressWarnings("unchecked")
    public EmcMonitorSchemeCostDao getDao() {
        return monitorSchemeCostDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean wrapper) {
        EmcMonitorSchemeCostBean monitorSchemeCostBean = wrapper.parseUnique(EmcMonitorSchemeCostBean.class);
        monitorSchemeCostBean.setId(ApplicationContextHelper.getNextIdentity());
        monitorSchemeCostBean.setMonitorTmplId(monitorSchemeCostBean.getMonitorTmplId());
        monitorSchemeCostBean.setSchemeId(monitorSchemeCostBean.getSchemeId());
        monitorSchemeCostBean.setSchemeName(monitorSchemeCostBean.getSchemeName());
        getDao().insert(monitorSchemeCostBean);
        return monitorSchemeCostBean.getId();
    }
}
