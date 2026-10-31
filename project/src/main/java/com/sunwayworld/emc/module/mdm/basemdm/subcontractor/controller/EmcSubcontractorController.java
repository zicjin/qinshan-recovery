package com.sunwayworld.emc.module.mdm.basemdm.subcontractor.controller;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.framework.controller.GenericChoosePageController;
import com.sunwayworld.framework.controller.GenericEditListPageController;

/**
 * @author magh@sunwayworld.com
 * @date 2019-11-05
 */

@RequestMapping("/secure/emc/module/mdm/basemdm/subcontractors/page")
public interface EmcSubcontractorController extends GenericEditListPageController, GenericChoosePageController {
}
