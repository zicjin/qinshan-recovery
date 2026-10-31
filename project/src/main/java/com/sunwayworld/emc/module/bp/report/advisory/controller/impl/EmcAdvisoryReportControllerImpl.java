package com.sunwayworld.emc.module.bp.report.advisory.controller.impl;

import org.springframework.stereotype.Controller;

import com.sunwayworld.emc.module.bp.report.advisory.controller.EmcAdvisoryReportController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
* @author zhengxy@sunwayworld.com 2022年08月29日
*/
@Controller
@GikamBean
public class EmcAdvisoryReportControllerImpl implements EmcAdvisoryReportController {

    @Override
    @Log(value = "咨询报告编制列表页面", type = LogType.CONTROLLER)
    public String editListPage() {
        return "emc/module/bp/report/advisorys/emc-advisory-report-edit-list";
    }

    @Override
    @Log(value = "咨询报告审核列表页面", type = LogType.CONTROLLER)
    public String auditListPage() {
        return "emc/module/bp/report/advisorys/emc-advisory-report-audit-list";
    }

    @Override
    @Log(value = "咨询报告查询列表页面", type = LogType.CONTROLLER)
    public String searchListPage() {
        return "emc/module/bp/report/advisorys/emc-advisory-report-search-list";
    }
}
