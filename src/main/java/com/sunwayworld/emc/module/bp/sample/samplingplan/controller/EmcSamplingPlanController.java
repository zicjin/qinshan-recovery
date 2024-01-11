package com.sunwayworld.emc.module.bp.sample.samplingplan.controller;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.framework.controller.GenericEditListPageController;

/**
 * @author magt@sunwayworld.com 2019年10月12日
 */

@RequestMapping("/secure/emc/module/bp/sample/sampling-plans/page")
public interface EmcSamplingPlanController extends GenericEditListPageController {

    String sampleInfoPage();

    String monitorPropertyPage();

    String samplePlanQueryPage();

    String updateContainerPage();
}
