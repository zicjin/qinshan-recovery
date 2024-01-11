package com.sunwayworld.emc.module.mdm.basemdm.testcategory.controller;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.framework.controller.GenericChoosePageController;
import com.sunwayworld.framework.controller.GenericDetailPageController;
import com.sunwayworld.framework.controller.GenericEditListPageController;

/**
 * 测试类型 Controller
 * 
 */
@RequestMapping("/secure/emc/module/mdm/basemdm/test-categories/page")
public interface EmcTestCategoryController extends GenericEditListPageController, GenericDetailPageController, GenericChoosePageController {

}
