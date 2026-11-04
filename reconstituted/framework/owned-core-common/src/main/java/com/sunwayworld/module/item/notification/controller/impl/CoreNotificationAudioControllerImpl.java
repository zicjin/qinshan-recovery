/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.item.notification.controller.impl;

import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.item.notification.controller.CoreNotificationAudioController;
import org.springframework.stereotype.Controller;

@Controller
@GikamBean
public class CoreNotificationAudioControllerImpl
implements CoreNotificationAudioController {
    @Override
    @Log(value="\u901a\u77e5\u8bed\u97f3\u5e93\u7f16\u5236\u5217\u8868\u9875", type=LogType.CONTROLLER)
    public String editListPage() {
        return "module/item/notification/notification-audios/core-notification-audio-edit-list";
    }
}
