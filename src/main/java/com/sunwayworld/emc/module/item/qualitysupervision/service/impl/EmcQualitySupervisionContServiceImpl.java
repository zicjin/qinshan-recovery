package com.sunwayworld.emc.module.item.qualitysupervision.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.item.qualitysupervision.bean.EmcQualitySupervisionContBean;
import com.sunwayworld.emc.module.item.qualitysupervision.dao.EmcQualitySupervisionContDao;
import com.sunwayworld.emc.module.item.qualitysupervision.service.EmcQualitySupervisionContService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;


/**
 * @author zhangft@sunwayworld.com
 * @date 2019-12-10
 */
@Repository
@GikamBean
public class EmcQualitySupervisionContServiceImpl implements EmcQualitySupervisionContService {
    @Autowired
    private EmcQualitySupervisionContDao qualitySupervisionContDao;

    @Override
    @SuppressWarnings("unchecked")
    public EmcQualitySupervisionContDao getDao() {
        return qualitySupervisionContDao;
    }

    @Override
    @Log(value = "新增质量监督计划内容", type = LogType.INSERT)
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(Long planId, RestJsonWrapperBean jsonWrapper) {
        EmcQualitySupervisionContBean qualitySupervisionCont = jsonWrapper.parseUnique(EmcQualitySupervisionContBean.class);
        qualitySupervisionCont.setId(ApplicationContextHelper.getNextIdentity());
        qualitySupervisionCont.setPlanId(planId);
        getDao().insert(qualitySupervisionCont);
        return qualitySupervisionCont.getId();
    }
}
