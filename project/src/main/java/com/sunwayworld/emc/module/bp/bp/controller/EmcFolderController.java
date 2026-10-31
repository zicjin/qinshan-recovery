package com.sunwayworld.emc.module.bp.bp.controller;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.framework.controller.GenericChoosePageController;
import com.sunwayworld.framework.controller.GenericDetailPageController;
import com.sunwayworld.framework.controller.GenericEditListPageController;

/**
 * @author zhangsy@sunwayworld.com
 * @date 2019-10-12
 */

@RequestMapping("/secure/emc/module/bp/bp/folders/page")
public interface EmcFolderController extends GenericEditListPageController, GenericChoosePageController, GenericDetailPageController {

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

    @RequestMapping("/container-info-batch")
    String batchOrderContainerTestPage();

    @RequestMapping("/order-item-group-edit-list")
    String orderItemGroupEditListPage();

    @RequestMapping("order-item-group-sample-edit-list")
    String orderItemGroupSampleEditListPage();

    @RequestMapping("/order-item-edit-list")
    String orderItemEditListPage();

    @RequestMapping("/check-project-order-item-group-addition")
    String checkProjectOrderItemGroupAdditionPage();

    @RequestMapping("/check-project-order-item-edit-list")
    String checkProjectOrderItemEditListPage();

    @RequestMapping("/review")
    String reviewPage();

}
