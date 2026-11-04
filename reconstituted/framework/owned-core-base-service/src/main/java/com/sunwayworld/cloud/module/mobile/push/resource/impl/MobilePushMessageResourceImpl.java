/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.push.resource.impl;

import com.sunwayworld.cloud.module.mobile.push.bean.MobilePushMessageBean;
import com.sunwayworld.cloud.module.mobile.push.resource.MobilePushMessageResource;
import com.sunwayworld.cloud.module.mobile.push.service.MobilePushMessageService;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.base.resource.AbstractGenericResource;
import com.sunwayworld.framework.support.choosable.resource.AbstractGenericChoosableResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@LogModule(value="\u79fb\u52a8\u7aef\u63a8\u9001\u6d88\u606f")
@RestController
@GikamBean
@RequestMapping(value={"/secure/core/module/sys/mobile/push_messages"})
public class MobilePushMessageResourceImpl
implements MobilePushMessageResource,
AbstractGenericResource<MobilePushMessageService, MobilePushMessageBean, Long>,
AbstractGenericChoosableResource<MobilePushMessageService, MobilePushMessageBean, Long> {
    @Autowired
    private MobilePushMessageService mobilePushMessageService;

    @Override
    public MobilePushMessageService getService() {
        return this.mobilePushMessageService;
    }

    @Override
    public void send(RestJsonWrapperBean wrapper) {
        this.getService().send(wrapper);
    }
}
