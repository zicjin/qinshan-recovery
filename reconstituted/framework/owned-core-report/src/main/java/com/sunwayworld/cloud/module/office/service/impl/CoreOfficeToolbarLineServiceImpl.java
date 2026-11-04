/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.office.service.impl;

import com.sunwayworld.cloud.module.office.persistent.dao.CoreOfficeToolbarLineDao;
import com.sunwayworld.cloud.module.office.service.CoreOfficeToolbarLineService;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreOfficeToolbarLineServiceImpl
implements CoreOfficeToolbarLineService {
    @Autowired
    private CoreOfficeToolbarLineDao coreOfficeToolbarLineDao;

    @Override
    public CoreOfficeToolbarLineDao getDao() {
        return this.coreOfficeToolbarLineDao;
    }
}
