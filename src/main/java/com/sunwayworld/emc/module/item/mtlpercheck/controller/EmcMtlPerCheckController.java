package com.sunwayworld.emc.module.item.mtlpercheck.controller;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.framework.controller.GenericAuditListPageController;
import com.sunwayworld.framework.controller.GenericEditListPageController;

@RequestMapping("/secure/emc/module/item/mtl-per-checks/page")
public interface EmcMtlPerCheckController extends GenericEditListPageController,GenericAuditListPageController {
}
