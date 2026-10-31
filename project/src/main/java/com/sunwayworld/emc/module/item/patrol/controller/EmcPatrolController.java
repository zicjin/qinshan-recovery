package com.sunwayworld.emc.module.item.patrol.controller;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.framework.controller.GenericChoosePageController;
import com.sunwayworld.framework.controller.GenericDetailPageController;
import com.sunwayworld.framework.controller.GenericEditListPageController;

/**
 * @author cuikxt@sunwayworld.com
 * @date 2019-11-09
 */

@RequestMapping("/secure/emc/module/item/patrols/page")
public interface EmcPatrolController extends GenericEditListPageController, GenericChoosePageController, GenericDetailPageController {

    String schemeEditListPage();

    String recordEditListPage();

    String handleEditListPage();
}
