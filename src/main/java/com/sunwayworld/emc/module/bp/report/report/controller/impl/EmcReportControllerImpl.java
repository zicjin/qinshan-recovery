package com.sunwayworld.emc.module.bp.report.report.controller.impl;

import org.springframework.stereotype.Controller;

import com.sunwayworld.emc.module.bp.report.report.controller.EmcReportController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author yuanhh@sunwayworld.com
 * @date 2019-10-18
 */

@Controller
@GikamBean
public class EmcReportControllerImpl implements EmcReportController {

    @Log(value = "报告编辑列表页", type = LogType.CONTROLLER)
    @Override
    public String editListPage() {
        return "emc/module/bp/report/reports/emc-report-edit-list";
    }

    @Log(value = "报告审核列表页", type = LogType.CONTROLLER)
    @Override
    public String auditListPage() {
        return "emc/module/bp/report/reports/emc-report-audit-list";
    }

    @Log(value = "报告回退页", type = LogType.CONTROLLER)
    @Override
    public String undoPage() {
        return "emc/module/bp/report/reports/emc-report-undo";
    }

    @Log(value = "回退批", type = LogType.CONTROLLER)
    @Override
    public String undoRunPage() {
        return "emc/module/bp/report/reports/emc-report-undo-run";
    }

    @Log(value = "编辑备注页", type = LogType.CONTROLLER)
    @Override
    public String editRemarkPage() {
        return "emc/module/bp/report/reports/emc-edit-remark";
    }

    @Log(value = "更换指标页", type = LogType.CONTROLLER)
    @Override
    public String updateTaskIndexPage() {
        return "emc/module/bp/report/reports/emc-order-task-update-index";
    }

    @Log(value = "报告查询列表页", type = LogType.CONTROLLER)
    @Override
    public String searchListPage() {
        return "emc/module/bp/report/reports/emc-report-search-list";
    }

    @Log(value = "报告信息修改页", type = LogType.CONTROLLER)
    @Override
    public String updateListPage() {
        return "emc/module/bp/report/reports/emc-report-update-list";
    }

    @Log(value = "选择数据出报告页", type = LogType.CONTROLLER)
    @Override
    public String regroupPage() {
        return "emc/module/bp/report/reports/emc-report-regroup";
    }
}
