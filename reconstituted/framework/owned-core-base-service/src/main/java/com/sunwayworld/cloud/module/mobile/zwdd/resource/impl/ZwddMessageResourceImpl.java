/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.zwdd.resource.impl;

import com.sunwayworld.cloud.module.mobile.zwdd.bean.ZwddMessageBean;
import com.sunwayworld.cloud.module.mobile.zwdd.resource.ZwddMessageResource;
import com.sunwayworld.cloud.module.mobile.zwdd.service.ZwddMessageService;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.base.resource.AbstractGenericResource;
import com.sunwayworld.framework.support.choosable.resource.AbstractGenericChoosableResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@LogModule(value="\u9489\u9489\u5fae\u5e94\u7528\u63a5\u53e3")
@RestController
@GikamBean
@RequestMapping(value={"/open/core/module/sys/mobile/zwdd/message"})
public class ZwddMessageResourceImpl
implements ZwddMessageResource,
AbstractGenericResource<ZwddMessageService, ZwddMessageBean, Long>,
AbstractGenericChoosableResource<ZwddMessageService, ZwddMessageBean, Long> {
    @Autowired
    private ZwddMessageService zwddMessageService;

    @Override
    public ZwddMessageService getService() {
        return this.zwddMessageService;
    }

    @Override
    public String sendMsg(String agentid, String userId, String title, String content, String data) {
        String status = this.getService().insertPushMessage(agentid, userId, title, content, data);
        return status;
    }
}
