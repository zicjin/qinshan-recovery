// 2026-09-24 从生产字节码反编译恢复（CFR 0.152）：生产新增类，历史源码缺失
package com.sunwayworld.emc.module.bp.report.report.controller;

import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(value={"/open/emc/module/bp/report/reports/page"})
public interface PfOpenReportController {
    @RequestMapping(value={"/select-report-test-conclusion"})
    public String selectReportTestConclusion();
}
