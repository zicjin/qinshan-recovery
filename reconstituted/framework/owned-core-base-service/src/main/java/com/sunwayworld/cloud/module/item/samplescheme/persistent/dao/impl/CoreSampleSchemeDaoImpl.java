/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.samplescheme.persistent.dao.impl;

import com.sunwayworld.cloud.module.item.samplescheme.bean.CoreSampleSchemeBean;
import com.sunwayworld.cloud.module.item.samplescheme.persistent.dao.CoreSampleSchemeDao;
import com.sunwayworld.cloud.module.item.samplescheme.persistent.mapper.CoreSampleSchemeMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreSampleSchemeDaoImpl
extends MybatisDaoSupport<CoreSampleSchemeBean, Long>
implements CoreSampleSchemeDao {
    @Autowired
    private CoreSampleSchemeMapper coreSampleSchemeMapper;

    public CoreSampleSchemeMapper getMapper() {
        return this.coreSampleSchemeMapper;
    }
}
