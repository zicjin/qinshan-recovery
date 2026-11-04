/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.at.controller.impl;

import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.sys.at.controller.CoreAuditTrailLogController;
import org.springframework.stereotype.Controller;

@Controller
@GikamBean
public class CoreAuditTrailLogControllerImpl
implements CoreAuditTrailLogController {
    @Override
    @Log(value="\u5ba1\u8ba1\u8ddf\u8e2a\u65e5\u5fd7\u67e5\u8be2\u9875\u9762", type=LogType.CONTROLLER)
    public String searchListPage() {
        return "module/sys/at-logs/core-at-log-search-list";
    }

    @Override
    @Log(value="\u5ba1\u8ba1\u8ddf\u8e2a\u67e5\u8be2\u9875\u9762", type=LogType.CONTROLLER)
    public String traceListPage() {
        return "module/sys/at-logs/core-at-log-trace-list";
    }
}
