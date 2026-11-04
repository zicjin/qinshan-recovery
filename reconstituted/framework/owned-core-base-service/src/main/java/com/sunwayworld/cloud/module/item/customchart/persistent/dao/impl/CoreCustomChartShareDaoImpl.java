/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.customchart.persistent.dao.impl;

import com.sunwayworld.cloud.module.item.customchart.bean.CoreCustomChartShareBean;
import com.sunwayworld.cloud.module.item.customchart.persistent.dao.CoreCustomChartShareDao;
import com.sunwayworld.cloud.module.item.customchart.persistent.mapper.CoreCustomChartShareMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreCustomChartShareDaoImpl
extends MybatisDaoSupport<CoreCustomChartShareBean, Long>
implements CoreCustomChartShareDao {
    @Autowired
    private CoreCustomChartShareMapper coreCustomChartShareMapper;

    public CoreCustomChartShareMapper getMapper() {
        return this.coreCustomChartShareMapper;
    }
}
