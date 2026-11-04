/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.item.notification.resource.impl;

import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.base.resource.AbstractGenericResource;
import com.sunwayworld.module.item.notification.bean.CoreNotificationTemplateBean;
import com.sunwayworld.module.item.notification.resource.CoreNotificationTemplateResource;
import com.sunwayworld.module.item.notification.service.CoreNotificationTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@LogModule(value="\u901a\u77e5\u4e2d\u5fc3\u6a21\u677f")
@RestController
@GikamBean
public class CoreNotificationTemplateResourceImpl
implements CoreNotificationTemplateResource,
AbstractGenericResource<CoreNotificationTemplateService, CoreNotificationTemplateBean, String> {
    @Autowired
    private CoreNotificationTemplateService templateService;

    @Override
    public CoreNotificationTemplateService getService() {
        return this.templateService;
    }
}
