/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.chart.persistent.dao;

import com.sunwayworld.cloud.module.item.chart.bean.CoreChartBean;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.support.base.dao.GenericDao;
import java.util.List;
import java.util.Map;

public interface CoreChartDao
extends GenericDao<CoreChartBean, String> {
    public List<Map<String, Object>> selectSqlData(MapperParameter var1);
}
