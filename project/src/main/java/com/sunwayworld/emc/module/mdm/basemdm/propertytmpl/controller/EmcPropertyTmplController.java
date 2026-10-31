package com.sunwayworld.emc.module.mdm.basemdm.propertytmpl.controller;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.framework.controller.GenericChoosePageController;
import com.sunwayworld.framework.controller.GenericDetailPageController;
import com.sunwayworld.framework.controller.GenericEditListPageController;


/**
 * 属性设置 Controller
 * 
 */
@RequestMapping("/secure/emc/module/mdm/basemdm/property-tmpls/page")
public interface EmcPropertyTmplController extends GenericEditListPageController, GenericDetailPageController, GenericChoosePageController {

    @RequestMapping("/item-detail")
    public String fieldDetailPage();
    
}
