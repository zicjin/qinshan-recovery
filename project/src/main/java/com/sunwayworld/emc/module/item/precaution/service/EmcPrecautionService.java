package com.sunwayworld.emc.module.item.precaution.service;

import com.sunwayworld.emc.module.item.precaution.bean.EmcPrecautionBean;
import com.sunwayworld.framework.support.auditable.service.GenericAuditableService;
import com.sunwayworld.framework.support.base.service.GenericService;

/**
 * @author zhangft@sunwayworld.com
 * @date 2019-11-25
 */

public interface EmcPrecautionService extends GenericService<EmcPrecautionBean, Long>, 
                                              GenericAuditableService<EmcPrecautionBean, Long> {
}
