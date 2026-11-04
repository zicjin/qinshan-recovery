/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.customsurvey.service.impl;

import com.sunwayworld.cloud.module.qms.customsurvey.bean.CoreCustomSurveyItemBean;
import com.sunwayworld.cloud.module.qms.customsurvey.persistent.dao.CoreCustomSurveyItemDao;
import com.sunwayworld.cloud.module.qms.customsurvey.service.CoreCustomSurveyItemService;
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
public class CoreCustomSurveyItemServiceImpl
implements CoreCustomSurveyItemService {
    @Autowired
    private CoreCustomSurveyItemDao coreCustomSurveyItemDao;

    @Override
    public CoreCustomSurveyItemDao getDao() {
        return this.coreCustomSurveyItemDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.INSERT)
    @Audit(value="AUDIT.INSERT")
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        CoreCustomSurveyItemBean coreCustomSurveyItem = jsonWrapper.parseUnique(CoreCustomSurveyItemBean.class);
        coreCustomSurveyItem.setId(ApplicationContextHelper.getNextIdentity());
        this.getDao().insert(coreCustomSurveyItem);
        return coreCustomSurveyItem.getId();
    }
}
