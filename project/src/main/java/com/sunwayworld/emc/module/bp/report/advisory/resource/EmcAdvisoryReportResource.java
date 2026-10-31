package com.sunwayworld.emc.module.bp.report.advisory.resource;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.emc.module.bp.report.advisory.bean.EmcAdvisoryReportBean;
import com.sunwayworld.emc.module.bp.report.advisory.service.EmcAdvisoryReportService;
import com.sunwayworld.framework.support.auditable.resource.GenericAuditableResource;
import com.sunwayworld.framework.support.base.resource.GenericResource;

/**
* @author zhengxy@sunwayworld.com 2022年08月29日
*/
@RequestMapping("/secure/emc/module/bp/report/advisory-reports")
public interface EmcAdvisoryReportResource extends GenericResource<EmcAdvisoryReportService, EmcAdvisoryReportBean, Long>, GenericAuditableResource<EmcAdvisoryReportService, EmcAdvisoryReportBean, Long> {

}
