/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.lims.reporttemplate.service.impl;

import com.sunwayworld.cloud.lims.reporttemplate.bean.LimsReportTemplateFuncBean;
import com.sunwayworld.cloud.lims.reporttemplate.persistent.dao.LimsReportTemplateFuncDao;
import com.sunwayworld.cloud.lims.reporttemplate.service.LimsReportTemplateFuncService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@GikamBean
public class LimsReportTemplateFuncServiceImpl
implements LimsReportTemplateFuncService {
    @Autowired
    private LimsReportTemplateFuncDao limsReportTemplateFuncDao;

    @Override
    public LimsReportTemplateFuncDao getDao() {
        return this.limsReportTemplateFuncDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        LimsReportTemplateFuncBean limsReportTemplateFunc = jsonWrapper.parseUnique(LimsReportTemplateFuncBean.class);
        limsReportTemplateFunc.setId(ApplicationContextHelper.getNextIdentity());
        this.getDao().insert(limsReportTemplateFunc);
        return limsReportTemplateFunc.getId();
    }
}
