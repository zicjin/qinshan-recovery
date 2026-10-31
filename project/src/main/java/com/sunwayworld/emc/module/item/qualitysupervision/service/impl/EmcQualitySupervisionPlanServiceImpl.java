package com.sunwayworld.emc.module.item.qualitysupervision.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.item.qualitysupervision.bean.EmcQualitySupervisionPlanBean;
import com.sunwayworld.emc.module.item.qualitysupervision.dao.EmcQualitySupervisionPlanDao;
import com.sunwayworld.emc.module.item.qualitysupervision.service.EmcQualitySupervisionPlanService;
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
public class EmcQualitySupervisionPlanServiceImpl implements EmcQualitySupervisionPlanService {
    @Autowired
    private EmcQualitySupervisionPlanDao qualitySupervisionPlanDao;

    @Override
    @SuppressWarnings("unchecked")
    public EmcQualitySupervisionPlanDao getDao() {
        return qualitySupervisionPlanDao;
    }

    @Override
    @Log(value = "插入质量监督计划", type = LogType.INSERT)
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(Long supervisionId, RestJsonWrapperBean jsonWrapper) {
        EmcQualitySupervisionPlanBean qualitySupervisionPlan = jsonWrapper.parseUnique(EmcQualitySupervisionPlanBean.class);
        qualitySupervisionPlan.setId(ApplicationContextHelper.getNextIdentity());
        qualitySupervisionPlan.setSupervisionId(supervisionId);
        getDao().insert(qualitySupervisionPlan);
        return qualitySupervisionPlan.getId();
    }

}
