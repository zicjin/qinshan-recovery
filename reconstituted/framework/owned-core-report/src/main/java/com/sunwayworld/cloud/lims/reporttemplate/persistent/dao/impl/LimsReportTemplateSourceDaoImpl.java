/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.lims.reporttemplate.persistent.dao.impl;

import com.sunwayworld.cloud.lims.reporttemplate.bean.LimsReportTemplateSourceBean;
import com.sunwayworld.cloud.lims.reporttemplate.persistent.dao.LimsReportTemplateSourceDao;
import com.sunwayworld.cloud.lims.reporttemplate.persistent.mapper.LimsReportTemplateSourceMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class LimsReportTemplateSourceDaoImpl
extends MybatisDaoSupport<LimsReportTemplateSourceBean, Long>
implements LimsReportTemplateSourceDao {
    @Autowired
    private LimsReportTemplateSourceMapper limsReportTemplateSourceMapper;

    public LimsReportTemplateSourceMapper getMapper() {
        return this.limsReportTemplateSourceMapper;
    }

    @Override
    public List<Map<String, Object>> selectListBySql(MapperParameter parameter) {
        List<Map<String, Object>> mapList = this.getMapper().selectListBySql(parameter);
        return mapList;
    }
}
