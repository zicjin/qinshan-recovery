package com.sunwayworld.emc.module.item.leavereplenishs.controller;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.framework.controller.GenericAuditListPageController;
import com.sunwayworld.framework.controller.GenericEditListPageController;
import com.sunwayworld.framework.controller.GenericSearchListPageController;

/**
 * @author zhangft@sunwayworld.com
 * @date 2019-11-14
 */

@RequestMapping("/secure/emc/module/item/leave-replenishs/page")
public interface EmcLeaveReplenishController extends GenericEditListPageController,
                                                     GenericAuditListPageController,
                                                     GenericSearchListPageController {
}
