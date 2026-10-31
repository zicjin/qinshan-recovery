package com.sunwayworld.emc.module.item.staffabilitycheck.controller;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.framework.controller.GenericEditListPageController;
import com.sunwayworld.framework.controller.GenericSearchListPageController;

@RequestMapping("/secure/emc/module/item/staff-abality-checks/page")
public interface EmcStaffAbalityCheckController extends GenericEditListPageController, GenericSearchListPageController {
}
