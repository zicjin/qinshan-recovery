/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.monitor.monitor.persistent.dao;

import com.sunwayworld.cloud.module.monitor.monitor.bean.CoreMonitorMessageBean;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.support.base.dao.GenericDao;
import java.util.List;
import java.util.Map;

public interface CoreMonitorMessageDao
extends GenericDao<CoreMonitorMessageBean, Long> {
    public List<Map<String, Object>> selectLastMessages(MapperParameter var1);
}
