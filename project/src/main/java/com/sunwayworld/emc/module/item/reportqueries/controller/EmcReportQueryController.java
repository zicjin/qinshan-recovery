package com.sunwayworld.emc.module.item.reportqueries.controller;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.framework.controller.GenericAuditListPageController;
import com.sunwayworld.framework.controller.GenericEditListPageController;
import com.sunwayworld.framework.controller.GenericSearchListPageController;

/**
 * @author zhangft@sunwayworld.com
 * @date 2019-11-07
 */

@RequestMapping("/secure/emc/module/item/report-queries/page")
public interface EmcReportQueryController extends GenericEditListPageController, 
                                                    GenericAuditListPageController,
                                                    GenericSearchListPageController {
}
