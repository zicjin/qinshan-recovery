package com.sunwayworld.emc.module.mdm.basemdm.fixedasset.controller;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.framework.controller.GenericChoosePageController;
import com.sunwayworld.framework.controller.GenericDetailPageController;
import com.sunwayworld.framework.controller.GenericEditListPageController;

/**
 * @author cuikx@sunwayworld.com
 * @date 2019-11-25
 */

@RequestMapping("/secure/emc/module/mdm/basemdm/fixedassets/page")
public interface EmcFixedassetController extends GenericEditListPageController,
                                                     GenericChoosePageController,
                                                     GenericDetailPageController {
}
