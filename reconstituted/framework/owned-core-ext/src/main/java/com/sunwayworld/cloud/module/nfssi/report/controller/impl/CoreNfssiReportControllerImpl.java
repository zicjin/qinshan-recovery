/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.nfssi.report.controller.impl;

import com.sunwayworld.cloud.module.nfssi.report.controller.CoreNfssiReportController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.stereotype.Controller;

@LogModule(value="\u62bd\u6837\u5355\u62a5\u544a")
@Controller
@GikamBean
public class CoreNfssiReportControllerImpl
implements CoreNfssiReportController {
    @Override
    @Log(value="\u62bd\u6837\u5355\u62a5\u544a\u8be6\u60c5\u9875", type=LogType.CONTROLLER)
    public String detailPage() {
        return "module/nfssi/reports/core-nfssi-report-detail";
    }
}
