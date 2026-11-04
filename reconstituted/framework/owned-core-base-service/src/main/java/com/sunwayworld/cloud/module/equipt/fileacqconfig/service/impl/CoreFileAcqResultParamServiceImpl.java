/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.equipt.fileacqconfig.service.impl;

import com.sunwayworld.cloud.module.equipt.fileacqconfig.persistent.dao.CoreFileAcqResultParamDao;
import com.sunwayworld.cloud.module.equipt.fileacqconfig.service.CoreFileAcqResultParamService;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreFileAcqResultParamServiceImpl
implements CoreFileAcqResultParamService {
    @Autowired
    private CoreFileAcqResultParamDao coreFileAcqResultParamDao;

    @Override
    public CoreFileAcqResultParamDao getDao() {
        return this.coreFileAcqResultParamDao;
    }
}
