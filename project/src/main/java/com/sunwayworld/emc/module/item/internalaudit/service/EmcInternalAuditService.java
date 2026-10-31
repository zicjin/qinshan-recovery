package com.sunwayworld.emc.module.item.internalaudit.service;

import com.sunwayworld.emc.module.item.internalaudit.bean.EmcInternalAuditBean;
import com.sunwayworld.framework.support.auditable.service.GenericAuditableService;
import com.sunwayworld.framework.support.base.service.GenericService;

/**
 * @author cuikx@sunwayworld.com
 * @date 2019-11-12
 */

public interface EmcInternalAuditService extends GenericService<EmcInternalAuditBean, Long>,
                                                 GenericAuditableService<EmcInternalAuditBean, Long>{
}
