package com.sunwayworld.emc.module.mdm.equipt.filescript.controller;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.framework.controller.GenericChoosePageController;
import com.sunwayworld.framework.controller.GenericDetailPageController;
import com.sunwayworld.framework.controller.GenericEditListPageController;

/**
 * 设备脚本Controller
 */
@RequestMapping("/secure/emc/module/mdm/equipt/file-scripts/page")
public interface EmcEquiptFileScriptController extends GenericEditListPageController, GenericDetailPageController, GenericChoosePageController {

}