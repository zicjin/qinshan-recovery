package com.sunwayworld.emc.module.bp.report.advisory.resource.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.sunwayworld.emc.module.bp.report.advisory.resource.EmcAdvisoryReportResource;
import com.sunwayworld.emc.module.bp.report.advisory.service.EmcAdvisoryReportService;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
* @author zhengxy@sunwayworld.com 2022年08月29日
*/
@GikamBean
@RestController
@LogModule(value = "咨询报告")
public class EmcAdvisoryReportResourceImpl implements EmcAdvisoryReportResource {

    @Autowired
    private EmcAdvisoryReportService emcAdvisoryReportService;

    @Override
    public EmcAdvisoryReportService getService() {
        return emcAdvisoryReportService;
    }

}
