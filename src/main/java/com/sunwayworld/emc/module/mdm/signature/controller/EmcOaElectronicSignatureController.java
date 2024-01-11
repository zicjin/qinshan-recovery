package com.sunwayworld.emc.module.mdm.signature.controller;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.framework.controller.GenericAuditListPageController;
import com.sunwayworld.framework.controller.GenericEditListPageController;
import com.sunwayworld.framework.controller.GenericSearchListPageController;

/**
* @author zhengxy@sunwayworld.com 2020年12月30日
*/
@RequestMapping("/secure/emc/module/mdm/signatures/page")
public interface EmcOaElectronicSignatureController extends GenericEditListPageController, GenericAuditListPageController, GenericSearchListPageController {
}
