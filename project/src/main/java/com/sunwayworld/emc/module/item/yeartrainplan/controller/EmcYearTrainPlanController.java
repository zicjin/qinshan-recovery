package com.sunwayworld.emc.module.item.yeartrainplan.controller;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.framework.controller.GenericAuditListPageController;
import com.sunwayworld.framework.controller.GenericEditListPageController;

@RequestMapping("/secure/emc/module/item/year-train-plans/page")
public interface EmcYearTrainPlanController extends GenericEditListPageController,GenericAuditListPageController {
}
