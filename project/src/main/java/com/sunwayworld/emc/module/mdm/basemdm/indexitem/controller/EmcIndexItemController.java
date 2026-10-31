package com.sunwayworld.emc.module.mdm.basemdm.indexitem.controller;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.framework.controller.GenericChoosePageController;
import com.sunwayworld.framework.controller.GenericEditListPageController;

/**
 * @author magh@sunwayworld.com
 * @date 2019-09-17
 */

@RequestMapping("/secure/emc/module/mdm/basemdm/index-items/page")
public interface EmcIndexItemController extends GenericEditListPageController, GenericChoosePageController {
}
