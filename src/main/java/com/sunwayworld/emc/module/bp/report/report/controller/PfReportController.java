package com.sunwayworld.emc.module.bp.report.report.controller;

import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author zhengxy@sunwayworld.com 2022年01月14日
 */
@RequestMapping("/secure/emc/module/bp/report/reports/page")
public interface PfReportController extends EmcReportController {

    @RequestMapping("/folder-choose-list")
    String folderChoosePage();

    @RequestMapping("/report-view-list")
    String reportViewPage();

    @RequestMapping("/ordertask-choose-list")
    String orderTaskChoosePage();

    @RequestMapping("/report-send-info")
    String reportSendPage();

    @RequestMapping("/browse-record")
    String browseReportRecordPage();

    @RequestMapping("/browse-all-report")
    String browseAllReportPage();
}
