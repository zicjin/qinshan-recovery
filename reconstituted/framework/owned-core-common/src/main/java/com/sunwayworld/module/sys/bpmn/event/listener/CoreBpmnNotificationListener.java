/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.bpmn.event.listener;

import com.sunwayworld.module.item.notification.helper.CoreNotificationHelper;
import com.sunwayworld.module.mdm.user.bean.CoreUserBean;
import com.sunwayworld.module.mdm.user.service.CoreUserService;
import com.sunwayworld.module.sys.bpmn.diagram.ProcessStatus;
import com.sunwayworld.module.sys.bpmn.event.CoreBpmnNotificationEvent;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix="sunway.bpmn.notification.default", name={"enabled"}, havingValue="true")
public class CoreBpmnNotificationListener
implements ApplicationListener<CoreBpmnNotificationEvent> {
    @Autowired
    private CoreUserService userService;

    @Override
    public void onApplicationEvent(CoreBpmnNotificationEvent event) {
        if (ProcessStatus.DRAFT_REJECTED.equals((Object)event.getProcessStatus()) || ProcessStatus.APPROVE_REJECTED.equals((Object)event.getProcessStatus()) || ProcessStatus.DRAFT_WITHDRAWED.equals((Object)event.getProcessStatus())) {
            CoreNotificationHelper.sendNotification("BPMN_REJECT", event.getNotifiedUserIdList(), this.getDefaultParamMap(event));
        } else {
            CoreNotificationHelper.sendNotification("BPMN_PASS", event.getNotifiedUserIdList(), this.getDefaultParamMap(event));
        }
    }

    private Map<String, Object> getDefaultParamMap(CoreBpmnNotificationEvent event) {
        HashMap<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("name", event.getName());
        paramMap.put("desc", event.getDesc());
        paramMap.put("fromTaskName", event.getFromTaskName());
        paramMap.put("toTaskName", event.getToTaskName());
        paramMap.put("lastAuditorName", ((CoreUserBean)this.userService.selectById(event.getLastAuditorId())).getUserName());
        return paramMap;
    }
}
