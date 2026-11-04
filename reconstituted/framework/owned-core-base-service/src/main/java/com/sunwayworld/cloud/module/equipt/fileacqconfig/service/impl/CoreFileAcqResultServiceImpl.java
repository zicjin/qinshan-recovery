/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.equipt.fileacqconfig.service.impl;

import com.sunwayworld.cloud.module.equipt.fileacqconfig.persistent.dao.CoreFileAcqResultDao;
import com.sunwayworld.cloud.module.equipt.fileacqconfig.service.CoreFileAcqResultService;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreFileAcqResultServiceImpl
implements CoreFileAcqResultService {
    @Autowired
    private CoreFileAcqResultDao coreFileAcqResultDao;

    @Override
    public CoreFileAcqResultDao getDao() {
        return this.coreFileAcqResultDao;
    }
}
