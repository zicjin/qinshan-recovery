/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.item.notification.resource.impl;

import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.base.resource.AbstractGenericResource;
import com.sunwayworld.module.item.notification.bean.CoreNotificationInstanceBean;
import com.sunwayworld.module.item.notification.bean.CoreNotificationInstanceFileBean;
import com.sunwayworld.module.item.notification.bean.CoreNotificationMessagePageDTO;
import com.sunwayworld.module.item.notification.resource.CoreNotificationInstanceResource;
import com.sunwayworld.module.item.notification.service.CoreNotificationInstanceService;
import com.sunwayworld.module.item.notification.service.CoreNotificationInstanceToService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@LogModule(value="\u901a\u77e5\u4e2d\u5fc3\u5b9e\u4f8b")
@RestController
@GikamBean
public class CoreNotificationInstanceResourceImpl
implements CoreNotificationInstanceResource,
AbstractGenericResource<CoreNotificationInstanceService, CoreNotificationInstanceBean, Long> {
    @Autowired
    private CoreNotificationInstanceService instanceService;
    @Autowired
    private CoreNotificationInstanceToService instanceToService;

    @Override
    public CoreNotificationInstanceService getService() {
        return this.instanceService;
    }

    @Override
    public void read(RestJsonWrapperBean wrapper) {
        this.instanceToService.read(wrapper);
    }

    @Override
    public void ignore(RestJsonWrapperBean wrapper) {
        this.instanceToService.ignore(wrapper);
    }

    @Override
    public CoreNotificationMessagePageDTO selectNotificationMessageList(RestJsonWrapperBean wrapper) {
        return this.instanceService.selectNotificationMessageList(wrapper);
    }

    @Override
    public Page<CoreNotificationInstanceFileBean> selectNotificationFilePagination(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return this.instanceService.selectNotificationFilePagination(id, wrapper);
    }
}
