package com.sunwayworld.emc.module.bp.edit.projectchange.controller;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.framework.controller.GenericAuditListPageController;
import com.sunwayworld.framework.controller.GenericEditListPageController;
import com.sunwayworld.framework.controller.GenericSearchListPageController;

/**
 * @author magh@sunwayworld.com
 * @date 2019-09-27
 */
@RequestMapping("/secure/emc/module/bp/edit/project-changes/page")
public interface EmcProjectChangeController extends GenericEditListPageController, GenericAuditListPageController, GenericSearchListPageController {

}
