package com.sunwayworld.emc.module.item.mtlapply.controller;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.framework.controller.GenericAuditListPageController;
import com.sunwayworld.framework.controller.GenericEditListPageController;
import com.sunwayworld.framework.controller.GenericSearchListPageController;

/**
 * @author zhengxy@sunwayworld.com 2020年02月24日
 */
@RequestMapping("/secure/emc/module/item/mtl-applies/page")
public interface EmcMtlApplyController extends GenericEditListPageController,
                                               GenericAuditListPageController,
                                               GenericSearchListPageController {
}
