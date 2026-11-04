/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.monitor.config.controller.impl;

import com.sunwayworld.cloud.module.monitor.config.controller.CoreMonitorConfigController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.stereotype.Controller;

@Controller
@GikamBean
public class CoreMonitorConfigControllerImpl
implements CoreMonitorConfigController {
    @Override
    @Log(value="\u7cfb\u7edf\u76d1\u63a7\u914d\u7f6e\u9875", type=LogType.CONTROLLER)
    public String detailPage() {
        return "module/sys/monitor/configs/core-system-monitor-config-warn-list";
    }
}
