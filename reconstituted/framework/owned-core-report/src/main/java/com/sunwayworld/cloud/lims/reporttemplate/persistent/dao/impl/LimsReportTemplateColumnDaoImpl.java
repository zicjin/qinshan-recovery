/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.lims.reporttemplate.persistent.dao.impl;

import com.sunwayworld.cloud.lims.reporttemplate.bean.LimsReportTemplateColumnBean;
import com.sunwayworld.cloud.lims.reporttemplate.persistent.dao.LimsReportTemplateColumnDao;
import com.sunwayworld.cloud.lims.reporttemplate.persistent.mapper.LimsReportTemplateColumnMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class LimsReportTemplateColumnDaoImpl
extends MybatisDaoSupport<LimsReportTemplateColumnBean, Long>
implements LimsReportTemplateColumnDao {
    @Autowired
    private LimsReportTemplateColumnMapper limsReportTemplateColumnMapper;

    public LimsReportTemplateColumnMapper getMapper() {
        return this.limsReportTemplateColumnMapper;
    }
}
