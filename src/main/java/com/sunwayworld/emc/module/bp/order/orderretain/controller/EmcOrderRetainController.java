package com.sunwayworld.emc.module.bp.order.orderretain.controller;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.framework.controller.GenericAuditListPageController;
import com.sunwayworld.framework.controller.GenericEditListPageController;
import com.sunwayworld.framework.controller.GenericSearchListPageController;

/**
* @author zhengxy@sunwayworld.com 2019年10月17日
*/
@RequestMapping("/secure/emc/module/bp/order/order-retains/page")
public interface EmcOrderRetainController extends GenericEditListPageController, GenericSearchListPageController, GenericAuditListPageController {

    @RequestMapping("/retain-choose-list")
    String orderRetainChooseListPage();
}
