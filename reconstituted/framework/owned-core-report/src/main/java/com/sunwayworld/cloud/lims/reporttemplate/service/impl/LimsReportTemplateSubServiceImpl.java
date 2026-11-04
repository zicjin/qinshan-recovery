/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.lims.reporttemplate.service.impl;

import com.sunwayworld.cloud.lims.reporttemplate.bean.LimsReportTemplateSubBean;
import com.sunwayworld.cloud.lims.reporttemplate.persistent.dao.LimsReportTemplateSubDao;
import com.sunwayworld.cloud.lims.reporttemplate.service.LimsReportTemplateSubService;
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
public class LimsReportTemplateSubServiceImpl
implements LimsReportTemplateSubService {
    @Autowired
    private LimsReportTemplateSubDao limsReportTemplateSubDao;

    @Override
    public LimsReportTemplateSubDao getDao() {
        return this.limsReportTemplateSubDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        LimsReportTemplateSubBean limsReportTemplateSub = jsonWrapper.parseUnique(LimsReportTemplateSubBean.class);
        limsReportTemplateSub.setId(ApplicationContextHelper.getNextIdentity());
        this.getDao().insert(limsReportTemplateSub);
        return limsReportTemplateSub.getId();
    }
}
