package com.sunwayworld.emc.module.mdm.purchapply.controller;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.framework.controller.GenericAuditListPageController;
import com.sunwayworld.framework.controller.GenericChoosePageController;
import com.sunwayworld.framework.controller.GenericEditListPageController;
import com.sunwayworld.framework.controller.GenericSearchListPageController;

/**
 * @author zhanghy@sunwayworld.com 2020-09-03
 */
@RequestMapping("/secure/emc/module/mdm/purch-applies/page")
public interface EmcPurchApplyController extends GenericEditListPageController, GenericChoosePageController, GenericAuditListPageController, GenericSearchListPageController {

}
