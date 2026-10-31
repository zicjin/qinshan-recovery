package com.sunwayworld.emc.module.mdm.bizmdm.stdsetcategory.controller;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.framework.controller.GenericEditListPageController;

/**
 * 标准设置分类Controller
 */
@RequestMapping("/secure/emc/module/mdm/bizmdm/std-set-categorys/page")
public interface EmcStdSetCategoryController extends GenericEditListPageController {

    @RequestMapping("/field-info/choose-list")
    String chooseFieldInfoPage();

    @RequestMapping("/stand-set-script-detail")
    String standSetScriptDetailPage();
}
