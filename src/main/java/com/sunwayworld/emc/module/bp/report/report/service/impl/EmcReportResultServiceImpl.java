package com.sunwayworld.emc.module.bp.report.report.service.impl;

import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.bp.report.report.bean.EmcReportResultBean;
import com.sunwayworld.emc.module.bp.report.report.dao.EmcReportResultDao;
import com.sunwayworld.emc.module.bp.report.report.service.EmcReportResultService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;

/**
 * @author yuanhh@sunwayworld.com
 * @date 2019-10-18
 */

@Repository
@GikamBean
public class EmcReportResultServiceImpl implements EmcReportResultService {
    @Autowired
    private EmcReportResultDao emcReportResultDao;

    @Override
    @SuppressWarnings("unchecked")
    public EmcReportResultDao getDao() {
        return emcReportResultDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        EmcReportResultBean emcReportResult = jsonWrapper.parseUnique(EmcReportResultBean.class);
        emcReportResult.setId(ApplicationContextHelper.getNextIdentity());
        getDao().insert(emcReportResult);
        return emcReportResult.getId();
    }

}
