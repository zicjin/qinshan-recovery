package com.sunwayworld.emc.module.item.leaveapplies.controller;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.framework.controller.GenericAuditListPageController;
import com.sunwayworld.framework.controller.GenericDetailPageController;
import com.sunwayworld.framework.controller.GenericEditListPageController;
import com.sunwayworld.framework.controller.GenericSearchListPageController;

/**
 * @author zhangft@sunwayworld.com
 * @date 2019-11-12
 */
@RequestMapping("/secure/emc/module/item/leave-applies/page")
public interface EmcLeaveApplyController extends GenericEditListPageController, 
                                                 GenericAuditListPageController,
                                                 GenericSearchListPageController,
                                                 GenericDetailPageController {
}
