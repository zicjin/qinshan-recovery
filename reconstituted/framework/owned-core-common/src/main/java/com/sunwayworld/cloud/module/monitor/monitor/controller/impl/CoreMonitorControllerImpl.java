/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.monitor.monitor.controller.impl;

import com.sunwayworld.cloud.module.monitor.monitor.controller.CoreMonitorController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.stereotype.Controller;

@Controller
@GikamBean
public class CoreMonitorControllerImpl
implements CoreMonitorController {
    @Override
    @Log(value="\u670d\u52a1\u5668\u4fe1\u606f\u7f16\u5236\u5217\u8868\u9875", type=LogType.CONTROLLER)
    public String editListPage() {
        return "module/monitor/monitors/core-monitor-edit-list";
    }
}
