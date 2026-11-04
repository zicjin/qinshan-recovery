/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.push.controller.impl;

import com.sunwayworld.cloud.module.mobile.push.controller.MobilePushController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.stereotype.Controller;

@Controller
@GikamBean
public class MobilePushControllerImpl
implements MobilePushController {
    @Override
    @Log(value="\u79fb\u52a8\u6d88\u606f\u63a8\u9001->\u4e3b\u5217\u8868", type=LogType.CONTROLLER)
    public String editListPage() {
        return "module/sys/mobile/pushs/mobile-push-message-edit-list";
    }

    @Override
    @Log(value="\u79fb\u52a8\u6d88\u606f\u63a8\u9001->\u63a8\u9001\u7528\u6237\u9009\u62e9", type=LogType.CONTROLLER)
    public String choosePage() {
        return "module/sys/mobile/pushs/mobile-push-user-choose-list";
    }
}
