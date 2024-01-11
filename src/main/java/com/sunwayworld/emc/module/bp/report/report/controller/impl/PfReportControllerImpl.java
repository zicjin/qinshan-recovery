package com.sunwayworld.emc.module.bp.report.report.controller.impl;

import org.springframework.stereotype.Controller;

import com.sunwayworld.emc.module.bp.report.report.controller.PfReportController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author zhengxy@sunwayworld.com 2022年01月14日
 */
@Controller
@GikamBean
public class PfReportControllerImpl extends EmcReportControllerImpl implements PfReportController {

    @Override
    @Log(value = "报告编辑-点位选择页面", type = LogType.CONTROLLER)
    public String folderChoosePage() {
        return "emc/module/bp/report/reports/emc-report-folder-choose-list";
    }

    @Override
    @Log(value = "报告报表查询", type = LogType.CONTROLLER)
    public String reportViewPage() {
        return "emc/module/bp/report/reports/emc-report-view-list";
    }

    @Override
    @Log(value = "报告编辑-测试选择页面", type = LogType.CONTROLLER)
    public String orderTaskChoosePage() {
        return "emc/module/bp/report/reports/emc-report-order-task-choose-list";
    }

    @Override
    @Log(value = "报告发送信息页眉", type = LogType.CONTROLLER)
    public String reportSendPage() {
        return "emc/module/bp/report/reports/emc-report-send-info";
    }

    @Override
    @Log(value = "查看报告界面", type = LogType.CONTROLLER)
    public String browseReportRecordPage() {
        return "emc/module/bp/report/reports/emc-browse-report-record-search-list";
    }

    @Override
    @Log(value = "查看所有报告页面", type = LogType.CONTROLLER)
    public String browseAllReportPage() {
        return "emc/module/bp/report/reports/emc-browse-all-report";
    }
}
