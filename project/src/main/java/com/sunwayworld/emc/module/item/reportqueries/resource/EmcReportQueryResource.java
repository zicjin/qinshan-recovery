package com.sunwayworld.emc.module.item.reportqueries.resource;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.emc.module.item.reportqueries.bean.EmcReportQueryBean;
import com.sunwayworld.emc.module.item.reportqueries.service.EmcReportQueryService;
import com.sunwayworld.framework.support.auditable.resource.GenericAuditableResource;
import com.sunwayworld.framework.support.base.resource.GenericResource;

/**
 * @author zhangft@sunwayworld.com
 * @date 2019-11-07
 */
@RequestMapping("/secure/emc/module/item/report-queries")
public interface EmcReportQueryResource extends GenericResource<EmcReportQueryService, EmcReportQueryBean, String>,
                                                GenericAuditableResource<EmcReportQueryService, EmcReportQueryBean, String> {
}
