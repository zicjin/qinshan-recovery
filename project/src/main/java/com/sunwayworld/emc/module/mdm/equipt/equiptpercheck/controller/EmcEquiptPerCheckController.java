package com.sunwayworld.emc.module.mdm.equipt.equiptpercheck.controller;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.framework.controller.GenericEditListPageController;
import com.sunwayworld.framework.controller.GenericSearchListPageController;

@RequestMapping("/secure/emc/module/mdm/equipt/equipt-per-checks/page")
public interface EmcEquiptPerCheckController extends GenericEditListPageController,  GenericSearchListPageController {

    String auditListPage();

    String prinAuditListPage();
}
