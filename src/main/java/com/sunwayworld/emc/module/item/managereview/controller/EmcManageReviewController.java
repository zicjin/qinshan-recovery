package com.sunwayworld.emc.module.item.managereview.controller;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.framework.controller.GenericAuditListPageController;
import com.sunwayworld.framework.controller.GenericEditListPageController;
import com.sunwayworld.framework.controller.GenericSearchListPageController;

@RequestMapping("/secure/emc/module/item/manage-reviews/page")
public interface EmcManageReviewController extends GenericEditListPageController, GenericAuditListPageController,GenericSearchListPageController {

}
