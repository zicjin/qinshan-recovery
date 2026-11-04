/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.lims.reporttemplate.resource;

import com.sunwayworld.cloud.lims.reporttemplate.bean.LimsReportTemplateBean;
import com.sunwayworld.cloud.lims.reporttemplate.bean.LimsReportTemplateColumnBean;
import com.sunwayworld.cloud.lims.reporttemplate.bean.LimsReportTemplateSourceBean;
import com.sunwayworld.cloud.lims.reporttemplate.service.LimsReportTemplateService;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.restful.data.RestValidationResultBean;
import com.sunwayworld.framework.support.activatable.resource.GenericActivatableResource;
import com.sunwayworld.framework.support.base.resource.GenericResource;
import com.sunwayworld.framework.support.choosable.resource.GenericChoosableResource;
import com.sunwayworld.framework.support.selectable.resource.GenericSelectableResource;
import com.sunwayworld.module.item.file.bean.CoreFileBean;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(value={"/secure/lims/module/report-templates"})
public interface LimsReportTemplateResource
extends GenericResource<LimsReportTemplateService, LimsReportTemplateBean, Long>,
GenericChoosableResource<LimsReportTemplateService, LimsReportTemplateBean, Long>,
GenericSelectableResource<LimsReportTemplateService, LimsReportTemplateBean, Long>,
GenericActivatableResource<LimsReportTemplateService, LimsReportTemplateBean, Long> {
    public Page<LimsReportTemplateSourceBean> selectTemplateSourcePaginationByReportTemplateId(Long var1, RestJsonWrapperBean var2);

    public LimsReportTemplateSourceBean selectTemplateSourceDetail(Long var1);

    public Long insertTemplateSource(Long var1, RestJsonWrapperBean var2);

    public void deleteTemplateSource(Long var1, RestJsonWrapperBean var2);

    public RestValidationResultBean validateSql(Long var1, String var2, String var3);

    public RestValidationResultBean validateSql(RestJsonWrapperBean var1);

    public Page<LimsReportTemplateColumnBean> selectTemplateColumnPaginationByReportTemplateId(Long var1, RestJsonWrapperBean var2);

    public String generatorReport(Long var1, RestJsonWrapperBean var2);

    public CoreFileBean generatorReport(Long var1, String var2, RestJsonWrapperBean var3);

    public void refreshSourceColumn(Long var1, RestJsonWrapperBean var2);

    public void copyTemplate(RestJsonWrapperBean var1);

    public String exportDataZip(RestJsonWrapperBean var1);

    public Long copyTemplateWithFile(RestJsonWrapperBean var1);
}
