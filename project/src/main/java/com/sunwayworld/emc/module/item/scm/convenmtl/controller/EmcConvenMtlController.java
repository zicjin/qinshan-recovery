package com.sunwayworld.emc.module.item.scm.convenmtl.controller;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.framework.controller.GenericAuditListPageController;
import com.sunwayworld.framework.controller.GenericDetailPageController;
import com.sunwayworld.framework.controller.GenericEditListPageController;

@RequestMapping("/secure/emc/module/item/scm/conven-mtls/page")
public interface EmcConvenMtlController extends GenericEditListPageController, GenericAuditListPageController, GenericDetailPageController {
}
