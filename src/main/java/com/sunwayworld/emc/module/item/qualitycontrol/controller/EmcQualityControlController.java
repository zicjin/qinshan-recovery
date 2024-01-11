
package com.sunwayworld.emc.module.item.qualitycontrol.controller;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.framework.controller.GenericAuditListPageController;
import com.sunwayworld.framework.controller.GenericEditListPageController;
import com.sunwayworld.framework.controller.GenericSearchListPageController;

/**
 * 质量控制计划Controller
 * 
 */
@RequestMapping("/secure/emc/module/item/quality-controls/page")
public interface EmcQualityControlController extends GenericEditListPageController, GenericAuditListPageController, GenericSearchListPageController{

}
