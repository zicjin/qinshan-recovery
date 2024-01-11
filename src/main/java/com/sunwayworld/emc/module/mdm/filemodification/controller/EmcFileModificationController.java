package com.sunwayworld.emc.module.mdm.filemodification.controller;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.framework.controller.GenericAuditListPageController;
import com.sunwayworld.framework.controller.GenericChoosePageController;
import com.sunwayworld.framework.controller.GenericEditListPageController;
import com.sunwayworld.framework.controller.GenericSearchListPageController;

/**
 * @author zhanghy@sunwayworld.com 2020年09月01日
 */
@RequestMapping("/secure/emc/module/mdm/file-modifications/page")
public interface EmcFileModificationController extends GenericEditListPageController, GenericChoosePageController, GenericAuditListPageController, GenericSearchListPageController {

}
