/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.lims.reporttemplate.persistent.dao.impl;

import com.sunwayworld.cloud.lims.reporttemplate.bean.LimsReportTemplateBean;
import com.sunwayworld.cloud.lims.reporttemplate.persistent.dao.LimsReportTemplateDao;
import com.sunwayworld.cloud.lims.reporttemplate.persistent.mapper.LimsReportTemplateMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class LimsReportTemplateDaoImpl
extends MybatisDaoSupport<LimsReportTemplateBean, Long>
implements LimsReportTemplateDao {
    @Autowired
    private LimsReportTemplateMapper limsReportTemplateMapper;

    public LimsReportTemplateMapper getMapper() {
        return this.limsReportTemplateMapper;
    }
}
