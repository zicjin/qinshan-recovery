package com.sunwayworld.emc.module.item.qualitysupervision.controller;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.framework.controller.GenericAuditListPageController;
import com.sunwayworld.framework.controller.GenericEditListPageController;
import com.sunwayworld.framework.controller.GenericSearchListPageController;

/**
 * @author zhangft@sunwayworld.com
 * @date 2019-12-10
 */
@RequestMapping("/secure/emc/module/item/quality-supervisions/page")
public interface EmcQualitySupervisionController extends GenericEditListPageController,
                                                         GenericAuditListPageController,
                                                         GenericSearchListPageController {
}
