package com.sunwayworld.emc.module.mdm.equipt.equiptacqscript.controller;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.framework.controller.GenericChoosePageController;
import com.sunwayworld.framework.controller.GenericEditListPageController;

@RequestMapping("/secure/emc/module/mdm/equipt/equipt-acq-scripts/page")
public interface EmcEquiptAcqScriptController extends GenericEditListPageController, GenericChoosePageController {

    @RequestMapping("/analy-result")
    public String analyResultPage();
}
