package com.sunwayworld.emc.module.mdm.basemdm.monitorpointcategory.controller;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.framework.controller.GenericChoosePageController;
import com.sunwayworld.framework.controller.GenericEditListPageController;

/**
 * @author magh@sunwayworld.com
 * @date 2019-09-10
 */

@RequestMapping("/secure/emc/module/mdm/basemdm/monitor-point-categories/page")
public interface EmcMonitorPointCategoryController extends GenericEditListPageController, GenericChoosePageController {
}
