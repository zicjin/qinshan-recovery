/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.lims.reporttemplate.service;

import com.sunwayworld.cloud.lims.reporttemplate.bean.LimsReportTemplateBean;
import com.sunwayworld.cloud.lims.reporttemplate.bean.LimsReportTemplateColumnBean;
import com.sunwayworld.cloud.lims.reporttemplate.bean.LimsReportTemplateSourceBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.restful.data.RestValidationResultBean;
import com.sunwayworld.framework.support.activatable.service.GenericActivatableService;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.framework.support.choosable.service.GenericChoosableService;
import com.sunwayworld.framework.support.selectable.service.GenericSelectableService;
import com.sunwayworld.module.item.file.bean.CoreFileBean;

public interface LimsReportTemplateService
extends GenericService<LimsReportTemplateBean, Long>,
GenericChoosableService<LimsReportTemplateBean, Long>,
GenericSelectableService<LimsReportTemplateBean, Long>,
GenericActivatableService<LimsReportTemplateBean, Long> {
    public Page<LimsReportTemplateSourceBean> selectTemplateSourcePaginationByReportTemplateId(Long var1, RestJsonWrapperBean var2);

    public Long insertTemplateSource(Long var1, RestJsonWrapperBean var2);

    public void deleteTemplateSource(Long var1, RestJsonWrapperBean var2);

    public Page<LimsReportTemplateColumnBean> selectTemplateColumnPaginationByTemplateSourceId(Long var1, RestJsonWrapperBean var2);

    public LimsReportTemplateSourceBean selectTemplateSourceDetail(Long var1);

    public RestValidationResultBean validateSql(Long var1, String var2, String var3);

    public RestValidationResultBean validateSql(RestJsonWrapperBean var1);

    public String generatorReport(Long var1, RestJsonWrapperBean var2);

    public void refreshSourceColumn(Long var1, RestJsonWrapperBean var2);

    public CoreFileBean generatorReport(Long var1, String var2, RestJsonWrapperBean var3);

    public void copyTemplate(RestJsonWrapperBean var1);

    public String exportDataZip(RestJsonWrapperBean var1);

    public Long copyTemplateWithFile(RestJsonWrapperBean var1);
}
