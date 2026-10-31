package com.sunwayworld.emc.module.item.consume.consume.controller;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.framework.controller.GenericEditListPageController;

@RequestMapping("/secure/emc/module/item/consume/consumes/page")
public interface EmcConsumeController extends GenericEditListPageController {

    @RequestMapping("/{id}/stocks/storage")
    String storagePage();
    
    @RequestMapping("/{id}/stocks/inventory")
    String inventoryPage();
    
    @RequestMapping("/{id}/stocks/abandon")
    String abandonPage();
    
    @RequestMapping("/{id}/stocks/recieve")
    String recievePage();
}
