/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.sys.monitor.pm.controller.impl;

import com.sunwayworld.cloud.module.sys.monitor.pm.controller.CoreSystemMonitorSqlController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.stereotype.Controller;

@Controller
@GikamBean
public class CoreSystemMonitorSqlControllerImpl
implements CoreSystemMonitorSqlController {
    @Override
    @Log(value="sql\u6027\u80fd\u76d1\u63a7\u7f16\u5236\u5217\u8868\u9875", type=LogType.CONTROLLER)
    public String editListPage() {
        return "module/sys/monitor/pms/core-system-monitor-pm-edit-list";
    }
}
