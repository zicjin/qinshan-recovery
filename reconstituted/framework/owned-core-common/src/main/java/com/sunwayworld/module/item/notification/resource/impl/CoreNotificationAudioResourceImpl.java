/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.item.notification.resource.impl;

import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.item.notification.resource.CoreNotificationAudioResource;
import com.sunwayworld.module.item.notification.service.CoreNotificationAudioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@LogModule(value="\u901a\u77e5\u8bed\u97f3\u5e93")
@RestController
@GikamBean
public class CoreNotificationAudioResourceImpl
implements CoreNotificationAudioResource {
    @Autowired
    private CoreNotificationAudioService coreNotificationAudioService;

    @Override
    public CoreNotificationAudioService getService() {
        return this.coreNotificationAudioService;
    }

    @Override
    @Log(value="\u65b0\u589e\u901a\u77e5\u8bed\u97f3\u5e93", type=LogType.INSERT)
    @RequestMapping(method={RequestMethod.POST})
    public Long insert(RestJsonWrapperBean wrapper) {
        return (Long)this.getService().insert(wrapper);
    }
}
