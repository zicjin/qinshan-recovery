package com.sunwayworld.emc.module.bp.bp.controller;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.framework.controller.GenericChoosePageController;
import com.sunwayworld.framework.controller.GenericEditListPageController;

@RequestMapping("/secure/emc/module/bp/bp/order-tasks/page")
public interface EmcOrderTaskController extends GenericEditListPageController, GenericChoosePageController {

    @RequestMapping("/run-qc-category")
    String resultRunChoosePage();

    @RequestMapping("/atlas")
    String atlasPage();

    @RequestMapping("/update-result-reason")
    String updateResultReasonPage();

}
