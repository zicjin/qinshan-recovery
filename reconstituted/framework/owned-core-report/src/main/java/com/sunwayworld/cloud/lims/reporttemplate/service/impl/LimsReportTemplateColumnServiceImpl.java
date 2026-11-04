/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.lims.reporttemplate.service.impl;

import com.sunwayworld.cloud.lims.reporttemplate.bean.LimsReportTemplateColumnBean;
import com.sunwayworld.cloud.lims.reporttemplate.persistent.dao.LimsReportTemplateColumnDao;
import com.sunwayworld.cloud.lims.reporttemplate.service.LimsReportTemplateColumnService;
import com.sunwayworld.cloud.lims.reporttemplate.service.LimsReportTemplateSourceService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@GikamBean
public class LimsReportTemplateColumnServiceImpl
implements LimsReportTemplateColumnService {
    @Autowired
    @Lazy
    private LimsReportTemplateSourceService limsReportTemplateSourceService;
    @Autowired
    private LimsReportTemplateColumnDao limsReportTemplateColumnDao;

    @Override
    public LimsReportTemplateColumnDao getDao() {
        return this.limsReportTemplateColumnDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        LimsReportTemplateColumnBean limsReportTemplateColumn = jsonWrapper.parseUnique(LimsReportTemplateColumnBean.class);
        limsReportTemplateColumn.setId(ApplicationContextHelper.getNextIdentity());
        this.getDao().insert(limsReportTemplateColumn);
        return limsReportTemplateColumn.getId();
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.INSERT)
    public void refreshSourceColumn(Long id, RestJsonWrapperBean wrapperBean) {
        String clear = wrapperBean.getParamValue("clear");
        if (StringUtils.equalsIgnoreCase("1", clear)) {
            LimsReportTemplateColumnBean deleteItem = new LimsReportTemplateColumnBean();
            deleteItem.setTemplateSourceId(id);
            this.getDao().deleteBy(deleteItem, "templateSourceId");
        }
        this.limsReportTemplateSourceService.refreshSourceColumns(id);
    }
}
