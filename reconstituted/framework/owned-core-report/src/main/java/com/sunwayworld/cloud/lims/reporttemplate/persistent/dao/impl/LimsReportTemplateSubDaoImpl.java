/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.lims.reporttemplate.persistent.dao.impl;

import com.sunwayworld.cloud.lims.reporttemplate.bean.LimsReportTemplateSubBean;
import com.sunwayworld.cloud.lims.reporttemplate.persistent.dao.LimsReportTemplateSubDao;
import com.sunwayworld.cloud.lims.reporttemplate.persistent.mapper.LimsReportTemplateSubMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class LimsReportTemplateSubDaoImpl
extends MybatisDaoSupport<LimsReportTemplateSubBean, Long>
implements LimsReportTemplateSubDao {
    @Autowired
    private LimsReportTemplateSubMapper limsReportTemplateSubMapper;

    public LimsReportTemplateSubMapper getMapper() {
        return this.limsReportTemplateSubMapper;
    }
}
