package com.sunwayworld.emc.module.item.car.controller;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.framework.controller.GenericChoosePageController;
import com.sunwayworld.framework.controller.GenericDetailPageController;
import com.sunwayworld.framework.controller.GenericEditListPageController;
import com.sunwayworld.framework.controller.GenericSearchListPageController;

/**
 * @author cuikx@sunwayworld.com
 * @date 2019-11-12
 */

@RequestMapping("/secure/emc/module/item/cars/page")
public interface EmcCarController extends GenericEditListPageController, GenericChoosePageController, GenericDetailPageController,GenericSearchListPageController {

    String applyEditListPage();

    String auditEditListPage();

    String recordEditListPage();
}
