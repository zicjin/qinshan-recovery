/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.item.notification.service.impl;

import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.item.notification.dao.CoreNotificationInstanceFileDao;
import com.sunwayworld.module.item.notification.service.CoreNotificationInstanceFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class CoreNotificationInstanceFileServiceImpl
implements CoreNotificationInstanceFileService {
    @Autowired
    private CoreNotificationInstanceFileDao instanceFileDao;

    @Override
    public CoreNotificationInstanceFileDao getDao() {
        return this.instanceFileDao;
    }
}
