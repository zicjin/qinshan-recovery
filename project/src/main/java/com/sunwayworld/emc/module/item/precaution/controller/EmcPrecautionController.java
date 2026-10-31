package com.sunwayworld.emc.module.item.precaution.controller;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.framework.controller.GenericAuditListPageController;
import com.sunwayworld.framework.controller.GenericEditListPageController;
import com.sunwayworld.framework.controller.GenericSearchListPageController;

/**
 * @author zhangft@sunwayworld.com
 * @date 2019-11-25
 */

@RequestMapping("/secure/emc/module/item/precautions/page")
public interface EmcPrecautionController extends GenericEditListPageController,
                                                 GenericAuditListPageController,
                                                 GenericSearchListPageController {
}
