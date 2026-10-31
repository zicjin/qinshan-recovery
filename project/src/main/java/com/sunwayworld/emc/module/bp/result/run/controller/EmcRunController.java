package com.sunwayworld.emc.module.bp.result.run.controller;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.framework.controller.GenericAuditListPageController;
import com.sunwayworld.framework.controller.GenericEditListPageController;

/**
 * 样品测试批Controller
 *
 */
@RequestMapping("/secure/emc/module/bp/result/runs/page")
public interface EmcRunController extends GenericEditListPageController, GenericAuditListPageController {

    @RequestMapping("/undo-order-task")
    String undoOrderTaskEditPage();

    @RequestMapping("/analysis-audit-list")
    String analysisAuditListPage();

    String correlationValidation();

    @RequestMapping("/project")
    String projectPage();

    @RequestMapping("/run-detail")
    String runDetailPage();

    @RequestMapping("/run-input-detail")
    String runInputDetailPage();

    @RequestMapping("/analysis-run-detail")
    String analysisRunDetailPage();

    @RequestMapping("/add-run-detail")
    String addRunDetail();

    @RequestMapping("/analysis-pretrial-list")
    String analysisPretrialListPage();

    @RequestMapping("/edit-item")
    String editItem();

    @RequestMapping("/method-files")
    String methodFiles();

    @RequestMapping("/historical-data")
    String historicalData();

    @RequestMapping("/result-input")
    String resultInput();

    @RequestMapping("/method-edit")
    String methodEdit();

    @RequestMapping("/batch-input")
    String batchInput();

    @RequestMapping("/edit-equipt")
    String editEquipt();

    @RequestMapping("/copy-blank-to")
    String copyBlankToPage();

    @RequestMapping("/copy-blank-spkc-to")
    String copyBlankSpkcToPage();

}
