package com.sunwayworld.emc.module.bp.order.orderhandover.controller;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.framework.controller.GenericSearchListPageController;

@RequestMapping("/secure/emc/module/bp/order/order-handovers/page")
public interface EmcOrderHandoverController extends GenericSearchListPageController{

    @RequestMapping("/sample-annex-list")
    String sampleAnnexListPage();
}
