/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.sys.monitor.config.controller.impl;

import com.sunwayworld.cloud.module.sys.monitor.config.controller.CoreSystemMonitorConfigController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.stereotype.Controller;

@Controller
@GikamBean
public class CoreSystemMonitorConfigControllerImpl
implements CoreSystemMonitorConfigController {
    @Override
    @Log(value="\u7cfb\u7edf\u76d1\u63a7\u81ea\u52a8\u6e05\u7406\u914d\u7f6e\u7f16\u5236\u5217\u8868\u9875", type=LogType.CONTROLLER)
    public String editListPage() {
        return "module/sys/monitor/configs/core-system-monitor-config-auto-clean-list";
    }
}
