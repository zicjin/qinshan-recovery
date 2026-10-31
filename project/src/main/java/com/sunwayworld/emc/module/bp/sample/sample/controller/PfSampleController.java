package com.sunwayworld.emc.module.bp.sample.sample.controller;


import org.springframework.web.bind.annotation.RequestMapping;


/**
 * @author licm@sunwayworld.com 2021年10月13日
 */

@RequestMapping("/secure/emc/module/bp/sample/samples/page")
public interface PfSampleController extends EmcSampleController {
    String vocBottleChoosePage();

    String soliOrderDescDetailPage();

    String orderStockPage();

    String orderStockDetailsPage();

    String oilFilmChoosePage();

    String sampleExpandDetailPage();

    String orderContainerChoosePage();

    String gasParameterInputPage();

    String sampleQuerySearchPage();
}
