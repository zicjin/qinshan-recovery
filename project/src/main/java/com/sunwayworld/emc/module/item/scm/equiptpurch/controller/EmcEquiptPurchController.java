package com.sunwayworld.emc.module.item.scm.equiptpurch.controller;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.framework.controller.GenericAuditListPageController;
import com.sunwayworld.framework.controller.GenericDetailPageController;
import com.sunwayworld.framework.controller.GenericEditListPageController;

@RequestMapping("/secure/emc/module/item/scm/equipt-purchs/page")
public interface EmcEquiptPurchController extends GenericEditListPageController, 
                                                     GenericAuditListPageController,GenericDetailPageController{
}
