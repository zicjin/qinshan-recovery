/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.equipt.fileacqconfig.service.impl;

import com.sunwayworld.cloud.module.equipt.fileacqconfig.persistent.dao.CoreFileAcqParamDao;
import com.sunwayworld.cloud.module.equipt.fileacqconfig.service.CoreFileAcqParamService;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreFileAcqParamServiceImpl
implements CoreFileAcqParamService {
    @Autowired
    private CoreFileAcqParamDao coreFileAcqParamDao;

    @Override
    public CoreFileAcqParamDao getDao() {
        return this.coreFileAcqParamDao;
    }
}
