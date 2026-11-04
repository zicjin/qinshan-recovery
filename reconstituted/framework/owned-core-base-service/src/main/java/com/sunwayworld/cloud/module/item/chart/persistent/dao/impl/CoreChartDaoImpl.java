/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.chart.persistent.dao.impl;

import com.sunwayworld.cloud.module.item.chart.bean.CoreChartBean;
import com.sunwayworld.cloud.module.item.chart.persistent.dao.CoreChartDao;
import com.sunwayworld.cloud.module.item.chart.persistent.mapper.CoreChartMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class CoreChartDaoImpl
extends MybatisDaoSupport<CoreChartBean, String>
implements CoreChartDao {
    @Autowired
    private CoreChartMapper chartMapper;

    public CoreChartMapper getMapper() {
        return this.chartMapper;
    }

    @Override
    public List<Map<String, Object>> selectSqlData(MapperParameter parameter) {
        return this.getMapper().selectSqlData(parameter);
    }
}
