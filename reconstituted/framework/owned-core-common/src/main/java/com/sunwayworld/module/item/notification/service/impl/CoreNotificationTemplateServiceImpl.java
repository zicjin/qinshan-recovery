/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.item.notification.service.impl;

import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.item.notification.dao.CoreNotificationTemplateDao;
import com.sunwayworld.module.item.notification.service.CoreNotificationTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class CoreNotificationTemplateServiceImpl
implements CoreNotificationTemplateService {
    @Autowired
    private CoreNotificationTemplateDao templateDao;

    @Override
    public CoreNotificationTemplateDao getDao() {
        return this.templateDao;
    }
}
