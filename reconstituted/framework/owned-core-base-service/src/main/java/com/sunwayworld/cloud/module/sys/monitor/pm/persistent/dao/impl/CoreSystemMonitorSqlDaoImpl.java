/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.sys.monitor.pm.persistent.dao.impl;

import com.sunwayworld.cloud.module.sys.monitor.pm.bean.CoreSystemMonitorSqlBean;
import com.sunwayworld.cloud.module.sys.monitor.pm.persistent.dao.CoreSystemMonitorSqlDao;
import com.sunwayworld.cloud.module.sys.monitor.pm.persistent.mapper.CoreSystemMonitorSqlMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreSystemMonitorSqlDaoImpl
extends MybatisDaoSupport<CoreSystemMonitorSqlBean, Long>
implements CoreSystemMonitorSqlDao {
    @Autowired
    private CoreSystemMonitorSqlMapper coreSystemMonitorSqlMapper;

    public CoreSystemMonitorSqlMapper getMapper() {
        return this.coreSystemMonitorSqlMapper;
    }
}
