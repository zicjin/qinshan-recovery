package com.sunwayworld.emc.module.mdm.basemdm.qccategory.controller;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.framework.controller.GenericChoosePageController;
import com.sunwayworld.framework.controller.GenericEditListPageController;

/**
 * QC类型 Controller
 * 
 */
@RequestMapping("/secure/emc/module/mdm/basemdm/qc-categories/page")
public interface EmcQcCategoryController extends GenericEditListPageController, GenericChoosePageController {

}
