package com.sunwayworld.emc.module.mdm.basemdm.sealrule.controller;

import com.sunwayworld.framework.controller.GenericChoosePageController;
import com.sunwayworld.framework.controller.GenericEditListPageController;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/secure/emc/module/mdm/basemdm/seal-rules/page")
public interface EmcSealRuleController extends GenericEditListPageController, GenericChoosePageController {

    @RequestMapping({"/details-choose-list"})
    String detailsChoosePage();
}
