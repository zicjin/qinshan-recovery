package com.sunwayworld.emc.module.bp.info.infoconfig.controller;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.framework.controller.GenericChoosePageController;
import com.sunwayworld.framework.controller.GenericEditListPageController;

@RequestMapping("/secure/emc/module/bp/info/info-configs/page")
public interface EmcInfoConfigController extends GenericEditListPageController, GenericChoosePageController {
}
