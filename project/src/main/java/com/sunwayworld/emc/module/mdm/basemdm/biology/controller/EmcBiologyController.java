package com.sunwayworld.emc.module.mdm.basemdm.biology.controller;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.framework.controller.GenericChoosePageController;
import com.sunwayworld.framework.controller.GenericDetailPageController;
import com.sunwayworld.framework.controller.GenericEditListPageController;

/**
 * @author cuikx@sunwayworld.com
 * @date 2019-11-28
 */

@RequestMapping("/secure/emc/module/mdm/basemdm/biologys/page")
public interface EmcBiologyController extends GenericEditListPageController,
                                                     GenericChoosePageController,
                                                     GenericDetailPageController {
}
