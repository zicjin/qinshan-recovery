/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.schedule.schedule.persistent.dao.impl;

import com.sunwayworld.cloud.module.schedule.bean.CoreScheduleTaskLogBean;
import com.sunwayworld.cloud.module.schedule.schedule.persistent.dao.CoreScheduleTaskLogDao;
import com.sunwayworld.cloud.module.schedule.schedule.persistent.mapper.CoreScheduleTaskLogMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class CoreScheduleTaskLogDaoImpl
extends MybatisDaoSupport<CoreScheduleTaskLogBean, Long>
implements CoreScheduleTaskLogDao {
    @Autowired
    private CoreScheduleTaskLogMapper mapper;

    public CoreScheduleTaskLogMapper getMapper() {
        return this.mapper;
    }
}
