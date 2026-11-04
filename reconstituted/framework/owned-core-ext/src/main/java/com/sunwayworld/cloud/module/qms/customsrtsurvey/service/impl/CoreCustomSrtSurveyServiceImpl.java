/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.customsrtsurvey.service.impl;

import com.sunwayworld.cloud.module.qms.customsrtsurvey.bean.CoreCustomSrtSurveyBean;
import com.sunwayworld.cloud.module.qms.customsrtsurvey.bean.CoreCustomSrtSurveyItemBean;
import com.sunwayworld.cloud.module.qms.customsrtsurvey.persistent.dao.CoreCustomSrtSurveyDao;
import com.sunwayworld.cloud.module.qms.customsrtsurvey.service.CoreCustomSrtSurveyItemService;
import com.sunwayworld.cloud.module.qms.customsrtsurvey.service.CoreCustomSrtSurveyService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@GikamBean
public class CoreCustomSrtSurveyServiceImpl
implements CoreCustomSrtSurveyService {
    @Autowired
    private CoreCustomSrtSurveyDao coreCustomSrtSurveyDao;
    @Autowired
    private CoreCustomSrtSurveyItemService coreCustomSrtSurveyItemService;

    @Override
    public CoreCustomSrtSurveyDao getDao() {
        return this.coreCustomSrtSurveyDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.INSERT)
    @Audit(value="AUDIT.INSERT")
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        CoreCustomSrtSurveyBean coreCustomSrtSurvey = jsonWrapper.parseUnique(CoreCustomSrtSurveyBean.class);
        coreCustomSrtSurvey.setId(ApplicationContextHelper.getNextIdentity());
        coreCustomSrtSurvey.setSurveyNo(ApplicationContextHelper.getNextSequence(this.getDao().getTable(), "BMCX"));
        this.getDao().insert(coreCustomSrtSurvey);
        return coreCustomSrtSurvey.getId();
    }

    @Override
    public Page<CoreCustomSrtSurveyItemBean> selectSrtSurveyItemPaginationByCustomSrtSurveyId(Long id, RestJsonWrapperBean wrapper) {
        return this.coreCustomSrtSurveyItemService.selectPaginationByFilter(SearchFilter.instance().match("SURVEYID", id).filter(MatchPattern.EQ), wrapper);
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.INSERT)
    @Audit(value="AUDIT.INSERT")
    public Long insertSrtSurveyItem(Long id, RestJsonWrapperBean wrapper) {
        List<CoreCustomSrtSurveyItemBean> coreCustomSrtSurveyItemList = wrapper.parse(CoreCustomSrtSurveyItemBean.class);
        coreCustomSrtSurveyItemList.forEach(e -> {
            e.setId(ApplicationContextHelper.getNextIdentity());
            e.setSurveyId(id);
        });
        this.coreCustomSrtSurveyItemService.getDao().insert(coreCustomSrtSurveyItemList);
        return coreCustomSrtSurveyItemList.get(0).getId();
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.DELETE)
    @Audit(value="AUDIT.DELETE")
    public void deleteSrtSurveyItem(Long id, RestJsonWrapperBean wrapper) {
        this.coreCustomSrtSurveyItemService.delete(wrapper);
    }
}
