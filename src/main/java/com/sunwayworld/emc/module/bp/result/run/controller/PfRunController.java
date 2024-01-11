package com.sunwayworld.emc.module.bp.result.run.controller;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.framework.controller.GenericChoosePageController;

/**
 * @author zhengxy@sunwayworld.com 2021年05月28日
 */
public interface PfRunController extends EmcRunController, GenericChoosePageController {

    @RequestMapping("/qc-audit-list")
    String qcAuditPage();

    @RequestMapping("/task-assign-list")
    String taskAssignPage();

    @RequestMapping("/run-weighing-list")
    String runWeighingPage();

    @RequestMapping("/run-pretreat-list")
    String runPretreatPage();

    @RequestMapping("/new-result-input")
    String newResultInput();

    @RequestMapping("/copy-weight")
    String copyWeightListPage();

    @RequestMapping("/order-preparations")
    String orderPreparation();

    @RequestMapping("/batch-update-order")
    String batchUpdateOrderPage();

    @RequestMapping("/equipt-crves")
    String equiptCrves();

    @RequestMapping("/equipt-crves-detail")
    String equiptCrvesDetail();

    @RequestMapping("/history-equipt-crves")
    String historyEquiptCrves();

    @RequestMapping("/history-equipt-crves-detail")
    String historyEquiptCrvesDetail();

    @RequestMapping("/order-task-choose-list")
    String orderTaskChoosePage();

    @RequestMapping("/qc-related-order-list")
    String qcRelatedOrderPage();

    @RequestMapping("/edit-task-equipt-list")
    String editTaskEquiptPage();

    @RequestMapping("/choose-acq-equipt")
    String chooseAcqEquipt();

    @RequestMapping("/run-annex-list")
    String runAnnexListPage();

    @RequestMapping("/run-temp-humidity")
    String runTempHumidityPage();

    @RequestMapping("/run-task-order")
    String runTaskOrderPage();

    @RequestMapping("/run-order-choose-list")
    String runOrderChoosePage();

    @RequestMapping("/run-temp-order")
    String runTempOrderPage();

    @RequestMapping("/run-temp-order-formetal")
    String runTempOrderForMetalPage();

    @RequestMapping("edit-run-temp-humidity")
    String editRunTempHumidityPage();

    @RequestMapping("/run-edit-temp-order")
    String runEditTempOrderPage();

    @RequestMapping("/batch-edit-item")
    String batchEditItemPage();

    @RequestMapping("/std-match-test")
    String stdMatchTestPage();

    @RequestMapping("/phys-chemic-record")
    String physChemicPage();

    @RequestMapping("/run-task-process-show")
    String taskProcessShowPage();

    @RequestMapping("/run-task-process-org-show")
    String taskProcessShowOrgPage();

    @RequestMapping("/oil-film-batch-add")
    String oilFilmBatchAddPage();

    @RequestMapping("/project-run-audit")
    String unAuditPage();

    @RequestMapping("/order-item-group-choose-list")
    String orderItemGroupChoosePage();

    @RequestMapping("/undo-run-audited")
    String undoAuditedPage();

    @RequestMapping("/browse-record")
    String browseRecordPage();

    @RequestMapping("/run-query-search-list")
    String runQuerySearchPage();
}
