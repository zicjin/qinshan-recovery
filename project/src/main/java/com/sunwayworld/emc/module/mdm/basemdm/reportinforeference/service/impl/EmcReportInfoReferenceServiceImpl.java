package com.sunwayworld.emc.module.mdm.basemdm.reportinforeference.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.mdm.basemdm.reportinforeference.bean.EmcReportInfoReferenceBean;
import com.sunwayworld.emc.module.mdm.basemdm.reportinforeference.dao.EmcReportInfoReferenceDao;
import com.sunwayworld.emc.module.mdm.basemdm.reportinforeference.service.EmcReportInfoReferenceService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;

/**
 * @author zhengxy@sunwayworld.com 2020年03月06日
 */
@Repository
public class EmcReportInfoReferenceServiceImpl implements EmcReportInfoReferenceService {

    @Autowired
    private EmcReportInfoReferenceDao emcReportInfoReferenceDao;

    @Override
    @SuppressWarnings("unchecked")
    public EmcReportInfoReferenceDao getDao() {
        return emcReportInfoReferenceDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean wrapperBean) {
        EmcReportInfoReferenceBean emcReportInfoReferenceBean = wrapperBean.parseUnique(EmcReportInfoReferenceBean.class);
        Long id = ApplicationContextHelper.getNextIdentity();
        emcReportInfoReferenceBean.setId(id);
        emcReportInfoReferenceBean.setSortNum(0L);
        getDao().insert(emcReportInfoReferenceBean);
        return id;
    }

    @Override
    public void copy(RestJsonWrapperBean wrapper) {
        List<EmcReportInfoReferenceBean> reportInfoList = wrapper.parse(EmcReportInfoReferenceBean.class);
        if (reportInfoList.isEmpty()) {
            return;
        }

        List<EmcReportInfoReferenceBean> newReportInfoList = new ArrayList<>();
        reportInfoList.forEach(r -> {
            EmcReportInfoReferenceBean reportInfo = new EmcReportInfoReferenceBean();
            BeanUtils.copyProperties(r, reportInfo);
            reportInfo.setSortNum(0L);
            reportInfo.setId(ApplicationContextHelper.getNextIdentity());
            newReportInfoList.add(reportInfo);
        });

        this.getDao().insert(newReportInfoList);
    }
}
