/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.item.notification.controller.impl;

import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.item.notification.controller.CoreNotificationInstanceController;
import org.springframework.stereotype.Controller;

@Controller
@GikamBean
public class CoreNotificationInstanceControllerImpl
implements CoreNotificationInstanceController {
    @Override
    @Log(value="\u901a\u77e5\u4e2d\u5fc3\u5b9e\u4f8b\u9875\u9762", type=LogType.CONTROLLER)
    public String editListPage() {
        return "module/item/notification/notification-instances/core-notification-instance-edit-list";
    }
}
