/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.lims.reporttemplate.persistent.dao;

import com.sunwayworld.cloud.lims.reporttemplate.bean.LimsReportTemplateSourceBean;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.support.base.dao.GenericDao;
import java.util.List;
import java.util.Map;

public interface LimsReportTemplateSourceDao
extends GenericDao<LimsReportTemplateSourceBean, Long> {
    public List<Map<String, Object>> selectListBySql(MapperParameter var1);
}
