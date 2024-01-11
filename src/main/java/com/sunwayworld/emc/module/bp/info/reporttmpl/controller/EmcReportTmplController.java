package com.sunwayworld.emc.module.bp.info.reporttmpl.controller;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.framework.controller.GenericChoosePageController;
import com.sunwayworld.framework.controller.GenericEditListPageController;

@RequestMapping("/secure/emc/module/bp/info/report-tmpls/page")
public interface EmcReportTmplController extends GenericEditListPageController, GenericChoosePageController {

    String lineEditPage();

}
