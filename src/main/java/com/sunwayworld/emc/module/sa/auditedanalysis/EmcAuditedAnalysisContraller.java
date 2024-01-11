package com.sunwayworld.emc.module.sa.auditedanalysis;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.framework.controller.GenericEditListPageController;

@RequestMapping("/secure/emc/module/sa/audited-analysiss/page")
public interface EmcAuditedAnalysisContraller extends GenericEditListPageController {

    @RequestMapping("/audited-analysis-run-detail")
    String auditAnalysisRunDetailPage();
}
