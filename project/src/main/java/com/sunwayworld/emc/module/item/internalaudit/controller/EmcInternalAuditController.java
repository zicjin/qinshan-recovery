package com.sunwayworld.emc.module.item.internalaudit.controller;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.framework.controller.GenericAuditListPageController;
import com.sunwayworld.framework.controller.GenericEditListPageController;
import com.sunwayworld.framework.controller.GenericSearchListPageController;

/**
 * @author cuikx@sunwayworld.com
 * @date 2019-11-12
 */
@RequestMapping("/secure/emc/module/item/internal-audits/page")
public interface EmcInternalAuditController extends GenericEditListPageController, 
                                                    GenericAuditListPageController,
                                                    GenericSearchListPageController {
}
