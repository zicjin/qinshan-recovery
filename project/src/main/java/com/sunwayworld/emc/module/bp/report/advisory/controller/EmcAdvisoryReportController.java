package com.sunwayworld.emc.module.bp.report.advisory.controller;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.framework.controller.GenericAuditListPageController;
import com.sunwayworld.framework.controller.GenericEditListPageController;
import com.sunwayworld.framework.controller.GenericSearchListPageController;

/**
* @author zhengxy@sunwayworld.com 2022年08月29日
*/
@RequestMapping("/secure/emc/module/bp/report/advisory-reports/page")
public interface EmcAdvisoryReportController extends GenericEditListPageController, GenericAuditListPageController, GenericSearchListPageController {
}
