package com.sunwayworld.emc.module.mdm.bizmdm.comparison.controller;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.framework.controller.GenericEditListPageController;

/**
* @author zhengxy@sunwayworld.com 2020年08月17日
*/
@RequestMapping("/secure/emc/module/mdm/bizmdm/comparisons/page")
public interface EmcComparisonController extends GenericEditListPageController {

    @RequestMapping("/result-list")
    String comparisonResultPage();
}
