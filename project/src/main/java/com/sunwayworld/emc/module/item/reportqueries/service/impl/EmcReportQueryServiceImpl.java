package com.sunwayworld.emc.module.item.reportqueries.service.impl;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.item.reportqueries.bean.EmcReportQueryBean;
import com.sunwayworld.emc.module.item.reportqueries.dao.EmcReportQueryDao;
import com.sunwayworld.emc.module.item.reportqueries.service.EmcReportQueryService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author zhangft@sunwayworld.com
 * @date 2019-11-07
 */

@Repository
@GikamBean
public class EmcReportQueryServiceImpl implements EmcReportQueryService {
    
    @Autowired
    private EmcReportQueryDao reportQueryDao;
    
    @Override
    @SuppressWarnings("unchecked")
    public EmcReportQueryDao getDao() {
        return reportQueryDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT) 
    public String insert(RestJsonWrapperBean jsonWrapper) {
        EmcReportQueryBean reportQuery = jsonWrapper.parseUnique(EmcReportQueryBean.class);
        reportQuery.setId(ApplicationContextHelper.getNextSequence(getDao().getTable()));
        getDao().insert(reportQuery);
        return reportQuery.getId();
    }
    
}
