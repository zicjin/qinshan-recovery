package com.sunwayworld.emc.module.mdm.basemdm.bizcategory.controller;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.framework.controller.GenericChoosePageController;
import com.sunwayworld.framework.controller.GenericDetailPageController;
import com.sunwayworld.framework.controller.GenericEditListPageController;

/**
 * 业务类型 Controller
 * 
 */
@RequestMapping("/secure/emc/module/mdm/basemdm/biz-categories/page")
public interface EmcBizCategoryController extends GenericEditListPageController, GenericDetailPageController, GenericChoosePageController {
    @RequestMapping("/org-permission")
    public String orgPermissionEditPage();
    
    @RequestMapping("/project-choose-list")
    public String projectChoosePage();
    
}
