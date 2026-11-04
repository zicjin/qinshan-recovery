/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.dingtalk.resource.impl;

import com.sunwayworld.cloud.module.mobile.dingtalk.bean.DingtalkMessageBean;
import com.sunwayworld.cloud.module.mobile.dingtalk.resource.DingtalkMessageResource;
import com.sunwayworld.cloud.module.mobile.dingtalk.service.DingtalkMessageService;
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
@RequestMapping(value={"/open/core/module/sys/mobile/dingtalk/message"})
public class DingtalkMessageResourceImpl
implements DingtalkMessageResource,
AbstractGenericResource<DingtalkMessageService, DingtalkMessageBean, Long>,
AbstractGenericChoosableResource<DingtalkMessageService, DingtalkMessageBean, Long> {
    @Autowired
    private DingtalkMessageService dingtalkMessageService;

    @Override
    public DingtalkMessageService getService() {
        return this.dingtalkMessageService;
    }

    @Override
    public String sendMsg(String agentid, String userId, String title, String content, String data) {
        String status = this.getService().insertPushMessage(agentid, userId, title, content, data);
        return status;
    }

    @Override
    public String sendNotificationMsg(String agentid, String userId, int code, String msg) {
        String status = this.getService().sendNotificationMsg(agentid, userId, code, msg);
        return status;
    }

    @Override
    public String clearMsg(String agentId, String userId, String recordId) {
        String status = this.getService().clearPushMessage(agentId, userId, recordId);
        return status;
    }
}
