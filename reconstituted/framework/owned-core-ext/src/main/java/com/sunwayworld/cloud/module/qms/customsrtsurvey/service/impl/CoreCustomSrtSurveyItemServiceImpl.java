/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.customsrtsurvey.service.impl;

import com.sunwayworld.cloud.module.qms.customsrtsurvey.bean.CoreCustomSrtSurveyItemBean;
import com.sunwayworld.cloud.module.qms.customsrtsurvey.persistent.dao.CoreCustomSrtSurveyItemDao;
import com.sunwayworld.cloud.module.qms.customsrtsurvey.service.CoreCustomSrtSurveyItemService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@GikamBean
public class CoreCustomSrtSurveyItemServiceImpl
implements CoreCustomSrtSurveyItemService {
    @Autowired
    private CoreCustomSrtSurveyItemDao coreCustomSrtSurveyItemDao;

    @Override
    public CoreCustomSrtSurveyItemDao getDao() {
        return this.coreCustomSrtSurveyItemDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.INSERT)
    @Audit(value="AUDIT.INSERT")
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        CoreCustomSrtSurveyItemBean coreCustomSrtSurveyItem = jsonWrapper.parseUnique(CoreCustomSrtSurveyItemBean.class);
        coreCustomSrtSurveyItem.setId(ApplicationContextHelper.getNextIdentity());
        this.getDao().insert(coreCustomSrtSurveyItem);
        return coreCustomSrtSurveyItem.getId();
    }
}
