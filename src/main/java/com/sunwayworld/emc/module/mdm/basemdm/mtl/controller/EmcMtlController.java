package com.sunwayworld.emc.module.mdm.basemdm.mtl.controller;

import com.sunwayworld.framework.controller.GenericSearchListPageController;
import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.framework.controller.GenericChoosePageController;
import com.sunwayworld.framework.controller.GenericEditListPageController;

@RequestMapping("/secure/emc/module/mdm/basemdm/mtls/page")
public interface EmcMtlController extends GenericEditListPageController, GenericSearchListPageController,
                                          GenericChoosePageController {

    @RequestMapping("/{id}/stocks/storage")
    String storagePage();

    @RequestMapping("/{id}/stocks/inventory")
    String inventoryPage();

    @RequestMapping("/{id}/stocks/abandon")
    String abandonPage();

    @RequestMapping("/{id}/stocks/recieve")
    String recievePage();

    @RequestMapping("/detail-choose-list")
    String detailChoosePage();

    @RequestMapping("/stock-choose-list")
    String stockChoosePage();

    @RequestMapping("/general-edit-list")
    String generalEditListPage();
}
