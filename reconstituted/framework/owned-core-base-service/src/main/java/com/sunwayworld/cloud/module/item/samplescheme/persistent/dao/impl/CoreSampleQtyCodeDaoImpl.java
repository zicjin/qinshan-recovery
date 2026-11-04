/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.samplescheme.persistent.dao.impl;

import com.sunwayworld.cloud.module.item.samplescheme.bean.CoreSampleQtyCodeBean;
import com.sunwayworld.cloud.module.item.samplescheme.persistent.dao.CoreSampleQtyCodeDao;
import com.sunwayworld.cloud.module.item.samplescheme.persistent.mapper.CoreSampleQtyCodeMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreSampleQtyCodeDaoImpl
extends MybatisDaoSupport<CoreSampleQtyCodeBean, Long>
implements CoreSampleQtyCodeDao {
    @Autowired
    private CoreSampleQtyCodeMapper coreSampleQtyCodeMapper;

    public CoreSampleQtyCodeMapper getMapper() {
        return this.coreSampleQtyCodeMapper;
    }
}
