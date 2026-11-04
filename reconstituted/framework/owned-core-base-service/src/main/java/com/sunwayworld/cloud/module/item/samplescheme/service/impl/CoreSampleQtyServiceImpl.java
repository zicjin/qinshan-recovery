/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.samplescheme.service.impl;

import com.sunwayworld.cloud.module.item.samplescheme.persistent.dao.CoreSampleQtyDao;
import com.sunwayworld.cloud.module.item.samplescheme.service.CoreSampleQtyService;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreSampleQtyServiceImpl
implements CoreSampleQtyService {
    @Autowired
    private CoreSampleQtyDao coreSampleQtyDao;

    @Override
    public CoreSampleQtyDao getDao() {
        return this.coreSampleQtyDao;
    }
}
