package com.sunwayworld.emc.module.bp.report.report.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.bp.report.report.bean.EmcReportSendInfoBean;
import com.sunwayworld.emc.module.bp.report.report.dao.EmcReportSendInfoDao;
import com.sunwayworld.emc.module.bp.report.report.service.EmcReportSendInfoService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;

/**
* @author zhengxy@sunwayworld.com 2022年01月14日
*/
@Repository
public class EmcReportSendInfoServiceImpl implements EmcReportSendInfoService {

    @Autowired
    private EmcReportSendInfoDao reportSendInfoDao;

    @Override
    @SuppressWarnings("unchecked")
    public EmcReportSendInfoDao getDao() {
        return reportSendInfoDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean wrapperBean) {
        EmcReportSendInfoBean sendInfo = wrapperBean.parseUnique(EmcReportSendInfoBean.class);
        Long id = ApplicationContextHelper.getNextIdentity();
        sendInfo.setId(id);
        getDao().insert(sendInfo);

        return id;
    }

}
