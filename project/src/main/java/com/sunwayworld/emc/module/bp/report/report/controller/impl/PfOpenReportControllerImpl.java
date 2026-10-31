// 2026-09-24 从生产字节码反编译恢复（CFR 0.152）：生产新增类，历史源码缺失
package com.sunwayworld.emc.module.bp.report.report.controller.impl;

import com.sunwayworld.emc.module.bp.report.report.controller.PfOpenReportController;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.stereotype.Controller;

@Controller
@GikamBean
public class PfOpenReportControllerImpl
implements PfOpenReportController {
    @Override
    public String selectReportTestConclusion() {
        return "emc/module/bp/report/reports/emc-report-test-conclusion-open";
    }
}
