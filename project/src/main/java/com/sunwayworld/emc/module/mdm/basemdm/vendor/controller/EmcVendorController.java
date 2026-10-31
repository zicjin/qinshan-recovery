package com.sunwayworld.emc.module.mdm.basemdm.vendor.controller;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.framework.controller.GenericChoosePageController;
import com.sunwayworld.framework.controller.GenericEditListPageController;

/**
 * 生产商供应商Controller
 */
@RequestMapping("/secure/emc/module/mdm/basemdm/vendors/page")
public interface EmcVendorController extends GenericEditListPageController, GenericChoosePageController {
}
