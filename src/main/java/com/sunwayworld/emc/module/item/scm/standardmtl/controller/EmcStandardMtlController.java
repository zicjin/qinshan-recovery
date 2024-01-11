package com.sunwayworld.emc.module.item.scm.standardmtl.controller;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.framework.controller.GenericAuditListPageController;
import com.sunwayworld.framework.controller.GenericDetailPageController;
import com.sunwayworld.framework.controller.GenericEditListPageController;

@RequestMapping("/secure/emc/module/item/scm/standard-mtls/page")
public interface EmcStandardMtlController extends GenericEditListPageController, GenericAuditListPageController ,GenericDetailPageController{
}
