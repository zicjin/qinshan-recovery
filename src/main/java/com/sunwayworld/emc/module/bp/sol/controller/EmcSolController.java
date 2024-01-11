package com.sunwayworld.emc.module.bp.sol.controller;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.framework.controller.GenericDetailPageController;
import com.sunwayworld.framework.controller.GenericEditListPageController;

@RequestMapping("/secure/emc/module/bp/sols/page")
public interface EmcSolController extends GenericEditListPageController, GenericDetailPageController {
}
