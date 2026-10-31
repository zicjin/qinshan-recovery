package com.sunwayworld.emc.module.item.qualitysupervision.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.item.qualitysupervision.bean.EmcQualitySupervisionBean;
import com.sunwayworld.emc.module.item.qualitysupervision.bean.EmcQualitySupervisionContBean;
import com.sunwayworld.emc.module.item.qualitysupervision.bean.EmcQualitySupervisionPlanBean;
import com.sunwayworld.emc.module.item.qualitysupervision.dao.EmcQualitySupervisionDao;
import com.sunwayworld.emc.module.item.qualitysupervision.service.EmcQualitySupervisionContService;
import com.sunwayworld.emc.module.item.qualitysupervision.service.EmcQualitySupervisionPlanService;
import com.sunwayworld.emc.module.item.qualitysupervision.service.EmcQualitySupervisionService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;


/**
 * @author zhangft@sunwayworld.com
 * @date 2019-12-10
 */
@Repository
@GikamBean
public class EmcQualitySupervisionServiceImpl implements EmcQualitySupervisionService {
    @Autowired
    private EmcQualitySupervisionDao qualitySupervisionDao;
    
    @Autowired
    private EmcQualitySupervisionPlanService qualitySupervisionPlanService;

    @Autowired
    private EmcQualitySupervisionContService qualitySupervisionContService;

    @Override
    @SuppressWarnings("unchecked")
    public EmcQualitySupervisionDao getDao() {
        return qualitySupervisionDao;
    }

    @Override
    @Log(value = "新增质量监督", type = LogType.SELECT)
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT) 
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        EmcQualitySupervisionBean qualitySupervision = jsonWrapper.parseUnique(EmcQualitySupervisionBean.class);
        qualitySupervision.setId(ApplicationContextHelper.getNextIdentity());
        qualitySupervision.setSupervisionCode(ApplicationContextHelper.getNextSequence("T_EMC_QUALITY_SUPERVISION", "ZLJD"));
        getDao().insert(qualitySupervision);
        return qualitySupervision.getId();
    }
    
    @Override
    @Log(value = "查询质量监督计划", type = LogType.SELECT)
    public Page<EmcQualitySupervisionPlanBean> selectQualitySupervisionPlanPaginationBySupervisionId(Long supervisionId, RestJsonWrapperBean wrapper) {
        return qualitySupervisionPlanService.selectPaginationByFilter(SearchFilter.instance().match("SUPERVISIONID", supervisionId).filter(MatchPattern.EQ), wrapper);
    }

    @Override
    @Log(value = "新增质量监督计划", type = LogType.INSERT)
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insertQualitySupervisionPlan(Long supervisionId, RestJsonWrapperBean wrapper) {
        return qualitySupervisionPlanService.insert(supervisionId, wrapper);
    }

    @Override
    @Log(value = "删除质量监督计划", type = LogType.DELETE)
    @Transactional
    @AuditTrailEntry(AuditTrailType.DELETE)
    public void deleteQualitySupervisionPlan(RestJsonWrapperBean wrapper) {
        qualitySupervisionPlanService.delete(wrapper);
    }

    @Override
    @Log(value = "根据质量监督计划查询质量监督内容", type = LogType.SELECT)
    public Page<EmcQualitySupervisionContBean> selectQualitySupervisionContPaginationByPlanId(Long planId, RestJsonWrapperBean wrapper) {
        return qualitySupervisionContService.selectPaginationByFilter(SearchFilter.instance().match("PLANID", planId).filter(MatchPattern.EQ), wrapper);
    }

    @Override
    @Log(value = "新增质量监督内容", type = LogType.INSERT)
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insertQualitySupervisionCont(Long planId, RestJsonWrapperBean wrapper) {
        return qualitySupervisionContService.insert(planId, wrapper);
    }

    @Override
    @Log(value = "删除质量监督内容", type = LogType.DELETE)
    @Transactional
    @AuditTrailEntry(AuditTrailType.DELETE)
    public void deleteQualitySupervisionCont(RestJsonWrapperBean wrapper) {
        qualitySupervisionContService.delete(wrapper);
    }
}
