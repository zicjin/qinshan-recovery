/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.remotecall.service.impl;

import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.sys.remotecall.dao.CoreRemoteCallResponseDao;
import com.sunwayworld.module.sys.remotecall.service.CoreRemoteCallResponseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class CoreRemoteCallResponseServiceImpl
implements CoreRemoteCallResponseService {
    @Autowired
    private CoreRemoteCallResponseDao remoteCallResponseDao;

    @Override
    public CoreRemoteCallResponseDao getDao() {
        return this.remoteCallResponseDao;
    }
}
