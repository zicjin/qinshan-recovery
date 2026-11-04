/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.chart.persistent.dao.impl;

import com.sunwayworld.cloud.module.item.chart.bean.CoreChartConfigBean;
import com.sunwayworld.cloud.module.item.chart.persistent.dao.CoreChartConfigDao;
import com.sunwayworld.cloud.module.item.chart.persistent.mapper.CoreChartConfigMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class CoreChartConfigDaoImpl
extends MybatisDaoSupport<CoreChartConfigBean, Long>
implements CoreChartConfigDao {
    @Autowired
    private CoreChartConfigMapper chartConfigMapper;

    public CoreChartConfigMapper getMapper() {
        return this.chartConfigMapper;
    }
}
