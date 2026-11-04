/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.nfssi.report.persistent.dao.impl;

import com.sunwayworld.cloud.module.nfssi.report.bean.CoreNfssiReportItemBean;
import com.sunwayworld.cloud.module.nfssi.report.persistent.dao.CoreNfssiReportItemDao;
import com.sunwayworld.cloud.module.nfssi.report.persistent.mapper.CoreNfssiReportItemMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreNfssiReportItemDaoImpl
extends MybatisDaoSupport<CoreNfssiReportItemBean, Long>
implements CoreNfssiReportItemDao {
    @Autowired
    private CoreNfssiReportItemMapper coreNfssiReportItemMapper;

    public CoreNfssiReportItemMapper getMapper() {
        return this.coreNfssiReportItemMapper;
    }
}
