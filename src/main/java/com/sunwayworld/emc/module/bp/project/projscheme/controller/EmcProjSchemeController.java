package com.sunwayworld.emc.module.bp.project.projscheme.controller;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.framework.controller.GenericEditListPageController;

/**
 * @author yuanhh@sunwayworld.com
 * @date 2019-10-08
 */

@RequestMapping("/secure/emc/module/bp/project/proj-schemes/page")
public interface EmcProjSchemeController extends GenericEditListPageController {

    @RequestMapping("/select-monitoring-point")
    public String selectMonitoringPointPage();

    @RequestMapping("/select-monitor-tmpl")
    public String selectMonitorTmplPage();

    @RequestMapping("select-monitor-tmpl-detection")
    public String selectMonitorTmplDetectionPage();

    @RequestMapping("/copy-share")
    public String copySharePage();

    @RequestMapping("/update-cycle-and-frequency")
    public String updateCycleAndFrequencyPage();

    @RequestMapping("/proj-order-batch-update")
    public String batchUpdatePage();

    @RequestMapping("/update-index")
    public String updateIndexPage();

    @RequestMapping("/proj-order-update-item-group")
    public String updateItemGroup();

    @RequestMapping("/init-scheme")
    public String initScheme();
}
