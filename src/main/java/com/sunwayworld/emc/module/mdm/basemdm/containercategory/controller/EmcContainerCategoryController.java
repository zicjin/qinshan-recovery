package com.sunwayworld.emc.module.mdm.basemdm.containercategory.controller;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.framework.controller.GenericChoosePageController;
import com.sunwayworld.framework.controller.GenericEditListPageController;

/**
 * 容器类型 Controller
 * 
 */
@RequestMapping("/secure/emc/module/mdm/basemdm/container-categories/page")
public interface EmcContainerCategoryController extends GenericEditListPageController, GenericChoosePageController {

	@RequestMapping("/maintenance-list")
    String maintenanceListPage();

    @RequestMapping("/tm-list")
    String chooseTMPage();

    @RequestMapping("/cm-copy")
    String copyCMPage();

    @RequestMapping("/test-method-choose")
    String chooseTestMethodPage();
    
    @RequestMapping("/container-choose")
    String chooseContainerPage();

    @RequestMapping("/cm-modify")
    String modifyContainerPage();
    
}
