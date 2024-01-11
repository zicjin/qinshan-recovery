package com.sunwayworld.emc.module.mdm.bizmdm.monitortmpl.controller;

import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author zhengxy@sunwayworld.com 2023年07月31日
 */
@RequestMapping("/secure/emc/module/mdm/bizmdm/monitor-tmpls/page")
public interface PfMonitorTmplController extends EmcMonitorTmplController {

    String monitorSchemeSamplingCostPage();
}
