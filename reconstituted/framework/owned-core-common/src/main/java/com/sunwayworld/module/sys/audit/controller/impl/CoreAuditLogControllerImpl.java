/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.audit.controller.impl;

import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.sys.audit.controller.CoreAuditLogController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@GikamBean
public class CoreAuditLogControllerImpl
implements CoreAuditLogController {
    @Override
    @RequestMapping(value={"/record-detail"})
    @Log(value="\u5ba1\u8ba1\u8ddf\u8e2a\u8be6\u60c5\u9875\u9762", type=LogType.CONTROLLER)
    public String recordDetailPage() {
        return "module/sys/audit/audit-logs/record-detail";
    }

    @Override
    @Log(value="\u5ba1\u8ba1\u8ddf\u8e2a\u67e5\u8be2\u9875\u9762", type=LogType.CONTROLLER)
    public String traceListPage() {
        return "module/sys/audit/audit-logs/core-audit-log-trace-list";
    }

    @Override
    @Log(value="\u5ba1\u8ba1\u8bb0\u5f55\u64cd\u4f5c\u8be6\u7ec6\u4fe1\u606f\u9875\u9762", type=LogType.CONTROLLER)
    public String queryListPage() {
        return "module/sys/audit/audit-logs/core-audit-log-activity-search-list";
    }

    @Override
    @Log(value="\u5ba1\u8ba1\u8ddf\u8e2a\u65e5\u5fd7\u67e5\u8be2\u9875\u9762\uff08\u65b0\u7248\uff09", type=LogType.CONTROLLER)
    public String searchListPage() {
        return "module/sys/audit/audit-logs/core-audit-log-serach-list";
    }
}
