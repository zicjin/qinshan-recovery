/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.chart.persistent.dao.impl;

import com.sunwayworld.cloud.module.item.chart.bean.CoreChartSearchConfigBean;
import com.sunwayworld.cloud.module.item.chart.persistent.dao.CoreChartSearchConfigDao;
import com.sunwayworld.cloud.module.item.chart.persistent.mapper.CoreChartSearchConfigMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class CoreChartSearchConfigDaoImpl
extends MybatisDaoSupport<CoreChartSearchConfigBean, Long>
implements CoreChartSearchConfigDao {
    @Autowired
    private CoreChartSearchConfigMapper chartSearchConfigMapper;

    public CoreChartSearchConfigMapper getMapper() {
        return this.chartSearchConfigMapper;
    }
}
