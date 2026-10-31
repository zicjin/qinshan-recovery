package com.sunwayworld.emc.module.mdm.file.fileapply.controller;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.framework.controller.GenericAuditListPageController;
import com.sunwayworld.framework.controller.GenericEditListPageController;
import com.sunwayworld.framework.controller.GenericSearchListPageController;

@RequestMapping("/secure/emc/module/mdm/file/file-applies/page")
public interface EmcFileApplyController extends GenericEditListPageController, GenericAuditListPageController, GenericSearchListPageController {

}
