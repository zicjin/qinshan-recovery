package com.sunwayworld.emc.module.item.inconformity.service;

import com.sunwayworld.emc.module.item.inconformity.bean.EmcInconformityBean;
import com.sunwayworld.framework.support.auditable.service.GenericAuditableService;
import com.sunwayworld.framework.support.base.service.GenericService;

/**
 * @author cuikx@sunwayworld.com
 * @date 2019-12-6
 */
public interface EmcInconformityService extends GenericService<EmcInconformityBean, Long>,
                                                GenericAuditableService<EmcInconformityBean, Long>{
}
