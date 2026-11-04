/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.schedule.schedule.persistent.dao.impl;

import com.sunwayworld.cloud.module.schedule.bean.CoreScheduleTaskConfigBean;
import com.sunwayworld.cloud.module.schedule.schedule.persistent.dao.CoreScheduleTaskConfigDao;
import com.sunwayworld.cloud.module.schedule.schedule.persistent.mapper.CoreScheduleTaskConfigMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class CoreScheduleTaskConfigDaoImpl
extends MybatisDaoSupport<CoreScheduleTaskConfigBean, String>
implements CoreScheduleTaskConfigDao {
    @Autowired
    private CoreScheduleTaskConfigMapper mapper;

    public CoreScheduleTaskConfigMapper getMapper() {
        return this.mapper;
    }
}
