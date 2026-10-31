package com.sunwayworld.emc.module.item.complaint.controller;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.framework.controller.GenericAuditListPageController;
import com.sunwayworld.framework.controller.GenericEditListPageController;
import com.sunwayworld.framework.controller.GenericSearchListPageController;

/**
 * @author guok@sunwayworld.com
 * @date 2019-12-13
 */

@RequestMapping("/secure/emc/module/item/complaints/page")
public interface EmcComplaintController extends GenericEditListPageController, GenericAuditListPageController, GenericSearchListPageController {

}
