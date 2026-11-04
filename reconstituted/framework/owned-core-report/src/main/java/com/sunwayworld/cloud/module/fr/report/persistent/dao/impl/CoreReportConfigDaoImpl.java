/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.fr.report.persistent.dao.impl;

import com.sunwayworld.cloud.module.fr.report.bean.CoreReportConfigBean;
import com.sunwayworld.cloud.module.fr.report.persistent.dao.CoreReportConfigDao;
import com.sunwayworld.cloud.module.fr.report.persistent.mapper.CoreReportConfigMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class CoreReportConfigDaoImpl
extends MybatisDaoSupport<CoreReportConfigBean, String>
implements CoreReportConfigDao {
    @Autowired
    private CoreReportConfigMapper coreReportConfigMapper;

    public CoreReportConfigMapper getMapper() {
        return this.coreReportConfigMapper;
    }
}
