package com.sunwayworld.emc.module.bp.report.report.controller;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.framework.controller.GenericAuditListPageController;
import com.sunwayworld.framework.controller.GenericEditListPageController;
import com.sunwayworld.framework.controller.GenericSearchListPageController;

/**
 * @author yuanhh@sunwayworld.com
 * @date 2019-10-18
 */

@RequestMapping("/secure/emc/module/bp/report/reports/page")
public interface EmcReportController extends GenericEditListPageController, GenericAuditListPageController, GenericSearchListPageController {
    @RequestMapping("/edit-remark")
    public String editRemarkPage();

    @RequestMapping("/order-task-update-index")
    public String updateTaskIndexPage();

    @RequestMapping("/undo")
    String undoPage();

    @RequestMapping("/undo-run")
    String undoRunPage();

    @RequestMapping("/update-list")
    String updateListPage();

    @RequestMapping("/regroup-report")
    String regroupPage();
}
