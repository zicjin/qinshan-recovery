package com.sunwayworld.emc.module.mdm.bizmdm.monitortmpl.controller;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.framework.controller.GenericChoosePageController;
import com.sunwayworld.framework.controller.GenericEditListPageController;

/**
 * @author magh@sunwayworld.com
 * @date 2019-09-19
 */

@RequestMapping("/secure/emc/module/mdm/bizmdm/monitor-tmpls/page")
public interface EmcMonitorTmplController extends GenericEditListPageController, GenericChoosePageController {

    @RequestMapping("/scheme-addition")
    public String schemeAdditionPage();

    @RequestMapping("/sample-category-edit-list")
    public String sampleCategoryEditListPage();

    @RequestMapping("/scheme-choose-list")
    public String schemeChoosePage();

    @RequestMapping("/scheme-method-choose-list")
    public String schemeMethodChoosePage();

    @RequestMapping("/scheme-detail")
    String schemeDetailPage();

    @RequestMapping("/scheme-item-group-choose-list")
    public String schemeItemGroupChoosePage();

    @RequestMapping("/order-category-choose-list")
    public String orderCategoryChoosePage();

    @RequestMapping("/sample-category-choose-list")
    public String sampleCategoryChoosePage();

    @RequestMapping("/scheme-test-item-choose-list")
    public String schemeTestItemChoosePage();
}
