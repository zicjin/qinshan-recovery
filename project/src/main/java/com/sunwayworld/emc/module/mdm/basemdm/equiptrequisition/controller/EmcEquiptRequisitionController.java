package com.sunwayworld.emc.module.mdm.basemdm.equiptrequisition.controller;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.framework.controller.GenericEditListPageController;
import com.sunwayworld.framework.controller.GenericAuditListPageController;
import com.sunwayworld.framework.controller.GenericSearchListPageController;
import com.sunwayworld.framework.controller.GenericDetailPageController;

@RequestMapping("/secure/emc/module/mdm/basemdm/equipt-requisitions/page")
public interface EmcEquiptRequisitionController extends GenericEditListPageController,
        GenericAuditListPageController, GenericSearchListPageController, GenericDetailPageController {
}
