package com.sunwayworld.emc.module.mdm.basemdm.area.controller;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.framework.controller.GenericChoosePageController;
import com.sunwayworld.framework.controller.GenericDetailPageController;
import com.sunwayworld.framework.controller.GenericEditListPageController;

/**
 * @author yuanhh@sunwayworld.com 2019年9月16日
 */

@RequestMapping("/secure/emc/module/mdm/basemdm/areas/page")
public interface EmcAreaController extends GenericEditListPageController, GenericChoosePageController, GenericDetailPageController {
}
