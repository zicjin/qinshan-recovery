package com.sunwayworld.emc.module.bp.delivery.controller;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.framework.controller.GenericEditListPageController;

/**
 * @author zhengxy@sunwayworld.com 2021年11月15日
 */
@RequestMapping("/secure/emc/module/bp/deliveries/page")
public interface EmcDeliveryController extends GenericEditListPageController {

    String orderChoosePage();

    String auditSearchListPage();

    String orderAcceptPage();
}
