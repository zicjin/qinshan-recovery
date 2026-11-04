/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.nfssi.report.persistent.dao.impl;

import com.sunwayworld.cloud.module.nfssi.report.bean.CoreNfssiReportBean;
import com.sunwayworld.cloud.module.nfssi.report.persistent.dao.CoreNfssiReportDao;
import com.sunwayworld.cloud.module.nfssi.report.persistent.mapper.CoreNfssiReportMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreNfssiReportDaoImpl
extends MybatisDaoSupport<CoreNfssiReportBean, String>
implements CoreNfssiReportDao {
    @Autowired
    private CoreNfssiReportMapper coreNfssiReportMapper;

    public CoreNfssiReportMapper getMapper() {
        return this.coreNfssiReportMapper;
    }
}
