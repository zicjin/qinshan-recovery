/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.samplescheme.persistent.dao.impl;

import com.sunwayworld.cloud.module.item.samplescheme.bean.CoreSampleQtyBean;
import com.sunwayworld.cloud.module.item.samplescheme.persistent.dao.CoreSampleQtyDao;
import com.sunwayworld.cloud.module.item.samplescheme.persistent.mapper.CoreSampleQtyMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreSampleQtyDaoImpl
extends MybatisDaoSupport<CoreSampleQtyBean, String>
implements CoreSampleQtyDao {
    @Autowired
    private CoreSampleQtyMapper coreSampleQtyMapper;

    public CoreSampleQtyMapper getMapper() {
        return this.coreSampleQtyMapper;
    }
}
