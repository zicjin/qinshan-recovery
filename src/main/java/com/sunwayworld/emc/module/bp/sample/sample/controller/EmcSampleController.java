package com.sunwayworld.emc.module.bp.sample.sample.controller;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.framework.controller.GenericAuditListPageController;
import com.sunwayworld.framework.controller.GenericChoosePageController;
import com.sunwayworld.framework.controller.GenericDetailPageController;
import com.sunwayworld.framework.controller.GenericEditListPageController;

/**
 * @author zhangsy@sunwayworld.com
 * @date 2019-10-12
 */

@RequestMapping("/secure/emc/module/bp/sample/samples/page")
public interface EmcSampleController extends GenericEditListPageController, GenericChoosePageController, GenericDetailPageController, GenericAuditListPageController {

    @RequestMapping("/info-detail")
    String sampleInfoDetailPage();

    @RequestMapping("/data-input-detail")
    String sampleDataInputDetailPage();

    @RequestMapping("/order-batch")
    String batchOrderPage();

    @RequestMapping("/sample-test-batch")
    String batchSampleTestPage();

    @RequestMapping("/order-choose-list")
    String orderChoosePage();

    @RequestMapping("/order-container-batch")
    String batchOrderContainerPage();

    @RequestMapping("/order-container")
    String orderContainerPage();

    @RequestMapping("/sample-annex-list")
    String sampleAnnexListPage();

    @RequestMapping("/sample-folder-pic-list")
    String sampleFolderPicPage();

    @RequestMapping("/order-prepare-container")
    String orderPrepareContainerPage();

    @RequestMapping("/repeat-number-set")
    String repeatNumberSet();

    @RequestMapping("/invalid-results")
    String invalidResult();

    @RequestMapping("/edit-item")
    String editItem();

    @RequestMapping("/sample-edit-info-detail")
    String sampleEditInfoDetailPage();

    @RequestMapping("/sample-edit-data-input-detail")
    String sampleEditDataInputDetailPage();

    @RequestMapping("/order-desc-detail")
    String orderDescDetailPage();

    @RequestMapping("/order-result-batch-input")
    String orderResultBatchInputPage();
    
    @RequestMapping("/result-input")
    String resultInputPage();

    @RequestMapping("/sampling-equipt")
    String samplingEquiptPage();

    @RequestMapping("/sample-detail")
    String sampleDetailPage();

    @RequestMapping("/sample-date")
    String sampleDatePage();
    
    


    @RequestMapping("/folder-dbs-desc-detail")
    String folderDBSDetailPage();

    @RequestMapping("/folder-dxs-desc-detail")
    String folderDXSDetailPage();

    @RequestMapping("/folder-fs-desc-detail")
    String folderFSDetailPage();

    @RequestMapping("/folder-tr-desc-detail")
    String folderTRDetailPage();

    @RequestMapping("/folder-cjw-desc-detail")
    String folderCJWDetailPage();
}
