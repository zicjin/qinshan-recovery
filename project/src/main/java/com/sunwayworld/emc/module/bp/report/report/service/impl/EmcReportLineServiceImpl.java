package com.sunwayworld.emc.module.bp.report.report.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.bp.report.report.bean.EmcReportLineBean;
import com.sunwayworld.emc.module.bp.report.report.dao.EmcReportLineDao;
import com.sunwayworld.emc.module.bp.report.report.service.EmcReportLineService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;

/**
* @author zhengxy@sunwayworld.com 2022年01月14日
*/
@Repository
public class EmcReportLineServiceImpl implements EmcReportLineService {

    @Autowired
    private EmcReportLineDao emcReportLineDao;

    @Override
    @SuppressWarnings("unchecked")
    public EmcReportLineDao getDao() {
        return emcReportLineDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean wrapperBean) {
        EmcReportLineBean reportLine = wrapperBean.parseUnique(EmcReportLineBean.class);
        Long id = ApplicationContextHelper.getNextIdentity();
        reportLine.setId(id);
        getDao().insert(reportLine);

        return id;
    }

}
