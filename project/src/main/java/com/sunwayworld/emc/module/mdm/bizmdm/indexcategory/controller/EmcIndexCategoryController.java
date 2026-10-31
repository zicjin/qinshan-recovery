package com.sunwayworld.emc.module.mdm.bizmdm.indexcategory.controller;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.framework.controller.GenericChoosePageController;
import com.sunwayworld.framework.controller.GenericDetailPageController;
import com.sunwayworld.framework.controller.GenericEditListPageController;

/**
 * 指标分类 Controller
 * 
 */
@RequestMapping("/secure/emc/module/mdm/bizmdm/index-categories/page")
public interface EmcIndexCategoryController extends GenericEditListPageController, GenericDetailPageController, GenericChoosePageController {

    @RequestMapping("/order-index-detail")
    public String orderIndexDetailPage();

    @RequestMapping("/index-preview-list")
    public String indexPreviewListPage();
}
