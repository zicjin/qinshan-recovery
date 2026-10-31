package com.sunwayworld.emc.module.mdm.crm.controller;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.framework.controller.GenericDetailPageController;
import com.sunwayworld.framework.controller.GenericEditListPageController;

/**
* @author zhengxy@sunwayworld.com 2021年08月26日
*/
@RequestMapping("/secure/emc/module/mdm/crm/contracts/page")
public interface CrmOpportunityContractController extends GenericEditListPageController, GenericDetailPageController {

    @RequestMapping("/order-choose-list")
    String orderChooseListPage();
}
