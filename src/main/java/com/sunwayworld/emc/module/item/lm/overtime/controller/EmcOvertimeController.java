package com.sunwayworld.emc.module.item.lm.overtime.controller;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.framework.controller.GenericAuditListPageController;
import com.sunwayworld.framework.controller.GenericChoosePageController;
import com.sunwayworld.framework.controller.GenericDetailPageController;
import com.sunwayworld.framework.controller.GenericEditListPageController;
import com.sunwayworld.framework.controller.GenericSearchListPageController;

/**
 * @author cuikx@sunwayworld.com
 * @date 2019-11-14
 */

@RequestMapping("/secure/emc/module/item/lm/overtimes/page")
public interface EmcOvertimeController extends GenericEditListPageController, GenericChoosePageController, GenericDetailPageController, GenericAuditListPageController ,GenericSearchListPageController{
}
