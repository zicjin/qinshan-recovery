/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.monitor.monitor.persistent.dao.impl;

import com.sunwayworld.cloud.module.monitor.monitor.bean.CoreMonitorMessageBean;
import com.sunwayworld.cloud.module.monitor.monitor.persistent.dao.CoreMonitorMessageDao;
import com.sunwayworld.cloud.module.monitor.monitor.persistent.mapper.CoreMonitorMessageMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.PersistableMetadataHelper;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreMonitorMessageDaoImpl
extends MybatisDaoSupport<CoreMonitorMessageBean, Long>
implements CoreMonitorMessageDao {
    @Autowired
    private CoreMonitorMessageMapper coreMonitorMessageMapper;

    public CoreMonitorMessageMapper getMapper() {
        return this.coreMonitorMessageMapper;
    }

    @Override
    public List<Map<String, Object>> selectLastMessages(MapperParameter parameter) {
        parameter.setTableName(PersistableMetadataHelper.getTableName(this.getType()));
        return this.getMapper().selectLastMessages(parameter);
    }
}
