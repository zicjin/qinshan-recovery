package com.sunwayworld.emc.module.item.quotation.detectionfactor.controller;

import com.sunwayworld.framework.controller.GenericChoosePageController;
import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.framework.controller.GenericEditListPageController;

@RequestMapping("/secure/emc/module/item/quotation/detectionfactors/page")
public interface EmcDetectionfactorController extends GenericEditListPageController, GenericChoosePageController {
    @RequestMapping("/batch-update-detection")
    public String batchUpdateDetectionPage();

    @RequestMapping("/add-custom-price")
    public String addCustomPricePage();
}
