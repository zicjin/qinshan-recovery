package com.sunwayworld.emc.module.bp.order.orderreceipt.controller;

import org.springframework.web.bind.annotation.RequestMapping;



@RequestMapping("/secure/emc/module/bp/order/order-receipts/page")
public interface PfOrderReceiptController extends EmcOrderReceiptController {

    @RequestMapping("/add-test-edit-list")
    String addTestEditPage();

    @RequestMapping("/add-test-search-list")
    String addTestSearchPage();
}
