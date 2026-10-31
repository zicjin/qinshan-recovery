package com.sunwayworld.emc.module.mdm.bizmdm.monitortmpl.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.mdm.bizmdm.monitortmpl.bean.EmcMonitorSchemeSamplingCostBean;
import com.sunwayworld.emc.module.mdm.bizmdm.monitortmpl.dao.EmcMonitorSchemeSamplingCostDao;
import com.sunwayworld.emc.module.mdm.bizmdm.monitortmpl.service.EmcMonitorSchemeSamplingCostService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@Repository
@GikamBean
public class EmcMonitorSchemeSamplingCostServiceImpl implements EmcMonitorSchemeSamplingCostService {
    @Autowired
    private EmcMonitorSchemeSamplingCostDao schemeSamplingCostDao;

    @Override
    @SuppressWarnings("unchecked")
    public EmcMonitorSchemeSamplingCostDao getDao() {
        return schemeSamplingCostDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean wrapper) {
        EmcMonitorSchemeSamplingCostBean schemeCostBean = wrapper.parseUnique(EmcMonitorSchemeSamplingCostBean.class);
        schemeCostBean.setId(ApplicationContextHelper.getNextIdentity());
        getDao().insert(schemeCostBean);

        return schemeCostBean.getId();
    }
}
