/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.sys.monitor.errorlog.persistent.dao.impl;

import com.sunwayworld.cloud.module.sys.monitor.errorlog.bean.CoreSystemMonitorErrorLogBean;
import com.sunwayworld.cloud.module.sys.monitor.errorlog.persistent.dao.CoreSystemMonitorErrorLogDao;
import com.sunwayworld.cloud.module.sys.monitor.errorlog.persistent.mapper.CoreSystemMonitorErrorLogMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreSystemMonitorErrorLogDaoImpl
extends MybatisDaoSupport<CoreSystemMonitorErrorLogBean, Long>
implements CoreSystemMonitorErrorLogDao {
    @Autowired
    private CoreSystemMonitorErrorLogMapper coreSystemMonitorErrorLogMapper;

    public CoreSystemMonitorErrorLogMapper getMapper() {
        return this.coreSystemMonitorErrorLogMapper;
    }
}
