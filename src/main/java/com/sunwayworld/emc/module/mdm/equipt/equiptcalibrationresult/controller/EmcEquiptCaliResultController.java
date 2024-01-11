package com.sunwayworld.emc.module.mdm.equipt.equiptcalibrationresult.controller;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.framework.controller.GenericAuditListPageController;
import com.sunwayworld.framework.controller.GenericEditListPageController;
import com.sunwayworld.framework.controller.GenericSearchListPageController;

@RequestMapping("/secure/emc/module/mdm/equipt/equipt-cali-results/page")
public interface EmcEquiptCaliResultController extends GenericEditListPageController,
        GenericAuditListPageController, GenericSearchListPageController {
}
