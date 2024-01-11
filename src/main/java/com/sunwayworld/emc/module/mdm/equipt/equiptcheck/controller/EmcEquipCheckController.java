package com.sunwayworld.emc.module.mdm.equipt.equiptcheck.controller;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.framework.controller.GenericAuditListPageController;
import com.sunwayworld.framework.controller.GenericEditListPageController;
import com.sunwayworld.framework.controller.GenericSearchListPageController;

@RequestMapping("/secure/emc/module/mdm/equipt/equip-checks/page")
public interface EmcEquipCheckController extends GenericEditListPageController, GenericAuditListPageController, GenericSearchListPageController {

    @RequestMapping("/appearance-list")
    public String appearanceEditListPage();

    @RequestMapping("/check-list")
    public String appearanceChooseListPage();
}
