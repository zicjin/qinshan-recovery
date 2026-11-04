/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.lims.reporttemplate.persistent.dao.impl;

import com.sunwayworld.cloud.lims.reporttemplate.bean.LimsReportTemplateFuncBean;
import com.sunwayworld.cloud.lims.reporttemplate.persistent.dao.LimsReportTemplateFuncDao;
import com.sunwayworld.cloud.lims.reporttemplate.persistent.mapper.LimsReportTemplateFuncMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class LimsReportTemplateFuncDaoImpl
extends MybatisDaoSupport<LimsReportTemplateFuncBean, Long>
implements LimsReportTemplateFuncDao {
    @Autowired
    private LimsReportTemplateFuncMapper limsReportTemplateFuncMapper;

    public LimsReportTemplateFuncMapper getMapper() {
        return this.limsReportTemplateFuncMapper;
    }
}
