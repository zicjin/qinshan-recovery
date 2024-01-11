package com.sunwayworld.emc.module.mdm.equipt.verification.controller;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.framework.controller.GenericAuditListPageController;
import com.sunwayworld.framework.controller.GenericEditListPageController;
import com.sunwayworld.framework.controller.GenericSearchListPageController;

@RequestMapping("/secure/emc/module/mdm/equipt/equip-verifications/page")
public interface EmcEquipVerificationController extends GenericEditListPageController, GenericAuditListPageController, GenericSearchListPageController {
}
