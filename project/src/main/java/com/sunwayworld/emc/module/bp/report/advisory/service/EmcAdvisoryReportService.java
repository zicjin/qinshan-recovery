package com.sunwayworld.emc.module.bp.report.advisory.service;

import java.util.List;

import com.sunwayworld.emc.module.bp.project.project.bean.EmcProjectBean;
import com.sunwayworld.emc.module.bp.report.advisory.bean.EmcAdvisoryReportBean;
import com.sunwayworld.framework.support.auditable.service.GenericAuditableService;
import com.sunwayworld.framework.support.base.service.GenericService;

/**
* @author zhengxy@sunwayworld.com 2022年08月29日
*/
public interface EmcAdvisoryReportService extends GenericService<EmcAdvisoryReportBean, Long>, GenericAuditableService<EmcAdvisoryReportBean, Long> {

    void createAdvisoryReport(List<EmcProjectBean> projectList);
}
