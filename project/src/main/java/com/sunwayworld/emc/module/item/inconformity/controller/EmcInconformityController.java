package com.sunwayworld.emc.module.item.inconformity.controller;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.framework.controller.GenericAuditListPageController;
import com.sunwayworld.framework.controller.GenericEditListPageController;
import com.sunwayworld.framework.controller.GenericSearchListPageController;

/**
 * @author cuikx@sunwayworld.com
 * @date 2019-12-6
 */
@RequestMapping("/secure/emc/module/item/inconformitys/page")
public interface EmcInconformityController extends GenericEditListPageController, 
                                                   GenericAuditListPageController,
                                                   GenericSearchListPageController {
}
