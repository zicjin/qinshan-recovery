package com.sunwayworld.emc.module.bp.order.orderreceipt.controller;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.framework.controller.GenericEditListPageController;

/**
 * @author zhengxy@sunwayworld.com 2019年10月14日
 */
@RequestMapping("/secure/emc/module/bp/order/order-receipts/page")
public interface EmcOrderReceiptController extends GenericEditListPageController {

    @RequestMapping("/qc-choose-list")
    String qcCategoryChooseListPage();

    @RequestMapping("/sample-annex-list")
    String sampleAnnexListPage();

    @RequestMapping("/order-receipt-container")
    String orderReceiptContainerPage();

    @RequestMapping("/order-container-batch")
    String batchOrderContainerPage();
}
