package com.sunwayworld.emc.module.mdm.basemdm.monitorability.controller;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.framework.controller.GenericChoosePageController;
import com.sunwayworld.framework.controller.GenericDetailPageController;
import com.sunwayworld.framework.controller.GenericEditListPageController;

/**
 * @author magt@sunwayworld.com
 * @date 2019-10-09
 */

@RequestMapping("/secure/emc/module/mdm/basemdm/monitor-abilities/page")
public interface EmcMonitorAbilityController extends GenericEditListPageController,
                                                     GenericChoosePageController,
                                                     GenericDetailPageController {

    @RequestMapping("/method-detail")
    String methodDetailPage();

}
