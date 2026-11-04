/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.sys.monitor.errorlog.controller.impl;

import com.sunwayworld.cloud.module.sys.monitor.errorlog.controller.CoreSystemMonitorErrorLogController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.stereotype.Controller;

@Controller
@GikamBean
public class CoreSystemMonitorErrorLogControllerImpl
implements CoreSystemMonitorErrorLogController {
    @Override
    @Log(value="\u9519\u8bef\u65e5\u5fd7\u7f16\u5236\u5217\u8868\u9875", type=LogType.CONTROLLER)
    public String editListPage() {
        return "module/sys/monitor/error-logs/core-system-monitor-errorlog-edit-list";
    }
}
