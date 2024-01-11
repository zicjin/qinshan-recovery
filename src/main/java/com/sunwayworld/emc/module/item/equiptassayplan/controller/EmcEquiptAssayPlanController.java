package com.sunwayworld.emc.module.item.equiptassayplan.controller;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.framework.controller.GenericAuditListPageController;
import com.sunwayworld.framework.controller.GenericEditListPageController;

@RequestMapping("/secure/emc/module/item/equipt-assay-plans/page")
public interface EmcEquiptAssayPlanController extends GenericEditListPageController,GenericAuditListPageController {
}
