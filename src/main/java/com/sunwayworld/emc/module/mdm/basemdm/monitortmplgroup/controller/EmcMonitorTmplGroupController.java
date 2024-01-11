package com.sunwayworld.emc.module.mdm.basemdm.monitortmplgroup.controller;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.framework.controller.GenericChoosePageController;
import com.sunwayworld.framework.controller.GenericEditListPageController;

/**
 * @author magh@sunwayworld.com 2019年9月6日
 */

@RequestMapping("/secure/emc/module/mdm/basemdm/monitor-tmpl-groups/page")
public interface EmcMonitorTmplGroupController extends GenericEditListPageController, GenericChoosePageController {
}
