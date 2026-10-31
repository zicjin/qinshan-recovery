package com.sunwayworld.emc.module.mdm.contract.controller;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.framework.controller.GenericChoosePageController;
import com.sunwayworld.framework.controller.GenericDetailPageController;
import com.sunwayworld.framework.controller.GenericEditListPageController;
import com.sunwayworld.framework.controller.GenericSearchListPageController;

@RequestMapping("/secure/emc/module/mdm/contracts/page")
public interface EmcContractController
        extends GenericEditListPageController, GenericDetailPageController, GenericSearchListPageController, GenericChoosePageController {

    @RequestMapping("/contract-copy-list")
    String copyListPage();

    @RequestMapping("/redact-list")
    public String redactListPage();
}
