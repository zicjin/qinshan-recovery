package com.sunwayworld.emc.module.item.improvement.controller;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.framework.controller.GenericAuditListPageController;
import com.sunwayworld.framework.controller.GenericEditListPageController;
import com.sunwayworld.framework.controller.GenericSearchListPageController;

/**
 * @author guok@sunwayworld.com
 * @date 2019-12-09
 */

@RequestMapping("/secure/emc/module/item/improvements/page")
public interface EmcImprovementController extends GenericEditListPageController, GenericAuditListPageController, GenericSearchListPageController {

}
