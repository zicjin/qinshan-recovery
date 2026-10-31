package com.sunwayworld.emc.module.mdm.equipt.equiptacqparam.controller;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.framework.controller.GenericChoosePageController;
import com.sunwayworld.framework.controller.GenericEditListPageController;

@RequestMapping("/secure/emc/module/mdm/equipt/equipt-acq-params/page")
public interface EmcEquiptAcqParamController extends GenericEditListPageController, GenericChoosePageController {

}
