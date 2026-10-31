package com.sunwayworld.emc.module.bp.result.curve.controller;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.framework.controller.GenericChoosePageController;

/**
 * @author yuanhh@sunwayworld.com
 * @date 2019-11-12
 */

@RequestMapping("/secure/emc/module/bp/result/curves/page")
public interface EmcCurveController extends GenericChoosePageController {
    @RequestMapping("/select-item-qc")
    public String selectItemQcPage();

    @RequestMapping("/data-result-input")
    public String resultInputPage();

    @RequestMapping("/view-curve-history")
    public String viewCurvePage();
}
