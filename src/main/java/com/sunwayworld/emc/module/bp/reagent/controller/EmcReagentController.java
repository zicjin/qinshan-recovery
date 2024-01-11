package com.sunwayworld.emc.module.bp.reagent.controller;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.framework.controller.GenericEditListPageController;

@RequestMapping("/secure/emc/module/bp/reagents/page")
public interface EmcReagentController extends GenericEditListPageController {
    @RequestMapping("/stock-choose-list")
    String stockChooseListPage();
}
