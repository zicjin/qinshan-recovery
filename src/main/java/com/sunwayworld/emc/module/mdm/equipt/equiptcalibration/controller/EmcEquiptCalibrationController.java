package com.sunwayworld.emc.module.mdm.equipt.equiptcalibration.controller;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.framework.controller.GenericEditListPageController;

/**
* @author zhengxy@sunwayworld.com 2020年08月09日
*/
@RequestMapping("/secure/emc/module/mdm/equipt/equipt-calibrations/page")
public interface EmcEquiptCalibrationController extends GenericEditListPageController {
    @RequestMapping("/detail-list")
    String detailListPage();
}
