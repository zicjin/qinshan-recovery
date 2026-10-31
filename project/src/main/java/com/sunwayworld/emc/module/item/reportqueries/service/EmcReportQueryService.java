package com.sunwayworld.emc.module.item.reportqueries.service;

import com.sunwayworld.emc.module.item.reportqueries.bean.EmcReportQueryBean;
import com.sunwayworld.framework.support.auditable.service.GenericAuditableService;
import com.sunwayworld.framework.support.base.service.GenericService;

/**
 * @author zhangft@sunwayworld.com
 * @date 2019-11-07
 */
public interface EmcReportQueryService extends GenericService<EmcReportQueryBean, String>,
                                               GenericAuditableService<EmcReportQueryBean, String> {
}
