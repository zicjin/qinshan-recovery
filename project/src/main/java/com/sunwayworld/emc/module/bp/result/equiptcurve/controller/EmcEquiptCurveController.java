package com.sunwayworld.emc.module.bp.result.equiptcurve.controller;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.framework.controller.GenericEditListPageController;
import com.sunwayworld.framework.controller.GenericChoosePageController;

@RequestMapping("/secure/emc/module/bp/result/equipt-curves/page")
public interface EmcEquiptCurveController extends GenericEditListPageController, GenericChoosePageController {

    @RequestMapping("/view-curve-history")
    public String viewCurvePage();

    @RequestMapping("/data-result-input")
    public String resultInputPage();

}
