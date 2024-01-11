package com.sunwayworld.emc.module.mdm.basemdm.envrecord.controller;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.framework.controller.GenericChoosePageController;
import com.sunwayworld.framework.controller.GenericDetailPageController;
import com.sunwayworld.framework.controller.GenericEditListPageController;

/**
 * 环境记录 Controller
 *
 */
@RequestMapping("/secure/emc/module/mdm/basemdm/env-records/page")
public interface EmcEnvRecordController extends GenericEditListPageController, GenericDetailPageController, GenericChoosePageController {

}
