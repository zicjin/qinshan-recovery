/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.lims.reporttemplate.service;

import com.sunwayworld.cloud.lims.reporttemplate.bean.LimsReportTemplateSourceBean;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.restful.data.RestValidationResultBean;
import com.sunwayworld.framework.support.base.service.GenericService;
import java.util.List;
import java.util.Map;

public interface LimsReportTemplateSourceService
extends GenericService<LimsReportTemplateSourceBean, Long> {
    public RestValidationResultBean validateTemplateSourceSql(String var1, String var2);

    public List<Map<String, Object>> selectListBySql(String var1, MapperParameter var2);

    public void refreshSourceColumns(Long var1);
}
