/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.customchart.persistent.dao.impl;

import com.sunwayworld.cloud.module.item.customchart.bean.CoreCustomChartBean;
import com.sunwayworld.cloud.module.item.customchart.persistent.dao.CoreCustomChartDao;
import com.sunwayworld.cloud.module.item.customchart.persistent.mapper.CoreCustomChartMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreCustomChartDaoImpl
extends MybatisDaoSupport<CoreCustomChartBean, Long>
implements CoreCustomChartDao {
    @Autowired
    private CoreCustomChartMapper coreCustomChartMapper;

    public CoreCustomChartMapper getMapper() {
        return this.coreCustomChartMapper;
    }
}
