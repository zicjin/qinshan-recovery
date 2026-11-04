/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.lims.reporttemplate.service;

import com.sunwayworld.cloud.lims.reporttemplate.bean.LimsReportTemplateColumnBean;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;

public interface LimsReportTemplateColumnService
extends GenericService<LimsReportTemplateColumnBean, Long> {
    public void refreshSourceColumn(Long var1, RestJsonWrapperBean var2);
}
