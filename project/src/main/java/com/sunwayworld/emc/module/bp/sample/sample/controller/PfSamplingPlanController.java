package com.sunwayworld.emc.module.bp.sample.sample.controller;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.emc.module.bp.sample.samplingplan.controller.EmcSamplingPlanController;

/**
 * @author licm@sunwayworld.com 2021年08月27日
 */

@RequestMapping("/secure/emc/module/bp/sample/sampling-plans/page")
public interface PfSamplingPlanController extends EmcSamplingPlanController {
    String carInfoPage();

    String addVocPage();

    String vocPage();

    String batchUpdateVocPage();

    String sampleBottlePage();

    String addSampleBottlePage();

    String sampleBottleChoosePage();

}
