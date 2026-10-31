package com.sunwayworld.emc.module.item.scm.purchmanage.controller;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.framework.controller.GenericAuditListPageController;
import com.sunwayworld.framework.controller.GenericSearchListPageController;

@RequestMapping("/secure/emc/module/item/scm/purch-manages/page")
public interface EmcPurchManageController extends GenericAuditListPageController ,GenericSearchListPageController{

}
